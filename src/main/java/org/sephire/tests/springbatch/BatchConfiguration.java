package org.sephire.tests.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.ItemProcessor;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public FlatFileItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new ClassPathResource("import-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("firstName", "lastName");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }

	@Bean
	public JdbcCursorItemReader<Person> databaseReader() {
		JdbcCursorItemReader<Person> dbReader = new JdbcCursorItemReader();
		dbReader.setDataSource(dataSource);
		dbReader.setSql("select first_name,last_name from people");
		dbReader.setRowMapper(new PersonRowMapper());

		return dbReader;
	}


	@Bean
	public ItemProcessor<Person,Person> inverseProcessor() {
        return new PersonItemProcessorInverse();
	}

    @Bean
    public ItemProcessor<Person,Person> processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public ItemProcessor<Person,Person> capitalizingProcessor() {
        return new CapitalizingPersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1())
				.next(step2())
				.next(step3())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

	public Step step2() {
        return stepBuilderFactory.get("step2")
                .<Person, Person>chunk(10)
                .reader(databaseReader())
                .processor(inverseProcessor())
                .writer(writer())
                .build();
	}

	public Step step3() {
        return stepBuilderFactory.get("step3")
                .<Person, Person>chunk(10)
                .reader(databaseReader())
                .processor(capitalizingProcessor())
                .writer(writer())
                .build();
	}

}
