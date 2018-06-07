package org.sephire.tests.springbatch;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PersonRowMapper implements RowMapper<Person> {
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";

	@Override
	public Person mapRow(ResultSet rs,int rowNum) throws SQLException {
		return new Person(rs.getString(FIRST_NAME),rs.getString(LAST_NAME));
	}
}
