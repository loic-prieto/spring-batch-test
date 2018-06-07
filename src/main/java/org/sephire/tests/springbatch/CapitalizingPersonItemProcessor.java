package org.sephire.tests.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CapitalizingPersonItemProcessor implements ItemProcessor<Person, Person> {
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(final Person person) {

        final String firstName = person.getFirstName().substring(0,1).toUpperCase() 
			+ person.getFirstName().substring(1).toLowerCase();
		final String lastName = person.getLastName().substring(0,1).toUpperCase() 
			+ person.getLastName().substring(1).toLowerCase();

        final Person transformedPerson = new Person(firstName, lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
