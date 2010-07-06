package test;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/*******************************************************************************
 * This is a part of the test bootstrap process,
 * 
 * specficially this bean adds the INET_NTOA function to the hsqldb database so
 * it behaves a bit more like MYSQL
 * 
 * @author cspocc
 * 
 */
public class PreDatabaseSetup implements InitializingBean {

	DataSource dataSource;

	public void afterPropertiesSet() throws Exception {
		Connection c = dataSource.getConnection();
		Statement s = c.createStatement();
		s
				.execute("CREATE ALIAS INET_NTOA for \"edu.bath.soak.util.TypeUtils.intToIPTxt\"");

	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
