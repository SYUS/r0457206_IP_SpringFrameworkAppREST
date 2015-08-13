package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import logger.DJLog;

import model.Person;

public class UserDb implements IUserDb {
        private static final String TABLE = "private.r0457206_ip_users";
        
	private Connection connection;
	private PreparedStatement statement;
	private Properties properties;
        
        private final DJLog logger = new DJLog();

	public UserDb(){}
	
	public UserDb(Properties properties) {
		try {
			Class.forName("org.postgresql.Driver");
			setProperties(properties);
                        logger.writeInfo("initialized DB remotely");
		} catch (Exception e) {
                        logger.writeFatal("exception in constructor remote userDb " + e.getMessage());
			throw new DbException(e.getMessage(), e);
		}
	}

        @Override
	public void delete(String email) {
		if (email == null) {
                        logger.writeWarning("Nothing to delete for remote DB, table " + TABLE);
			throw new DbException("Nothing to delete., table " + TABLE);
		}
		String sql = "DELETE FROM " + TABLE + " WHERE email=?";
		try {
			initializeStatement(sql);
			statement.setString(1, email);
			statement.execute();
                        logger.writeInfo("Executed SQL statement, for DELETE on remote DB, table " + TABLE);
		} catch (SQLException e) {
                        logger.writeFatal("exception in delete user remote userDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e);
		} finally {
			closeConnection();
		}
	}

        @Override
	public void update(Person person) {
		if (person == null) {
                        logger.writeWarning("Nothing to update for remote DB, table " + TABLE);
			throw new DbException("Nothing to update., table " + TABLE);
		}
		String sql = "UPDATE " + TABLE + " SET password=?, name=? WHERE email=?";
		try {
			initializeStatement(sql);
			statement.setString(1, person.getPassword());
			statement.setString(2, person.getName());
			statement.setString(3, person.getEmail());
			statement.execute();
                        logger.writeInfo("Executed SQL statement, for UPDATE on remote DB, table " + TABLE);
		} catch (SQLException e) {
                        logger.writeFatal("exception in update user remote userDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e);
		} finally {
			closeConnection();
		}
	}

        @Override
	public void add(Person person) {
		if (person == null) {
                        logger.writeWarning("Nothing to add for remote DB, table " + TABLE);
			throw new DbException("Nothing to add., table " + TABLE);
		}
		String sql = "INSERT INTO " + TABLE + " (name, email, password) VALUES (?, ?, ?)";
		try {
			initializeStatement(sql);
			statement.setString(1, person.getName());
			statement.setString(2, person.getEmail());
			statement.setString(3, person.getPassword());
			statement.execute();
                        logger.writeInfo("Executed SQL statement, for ADD on remote DB, table " + TABLE);
		} catch (SQLException e) {
                        logger.writeFatal("exception in add user remote userDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e);
		} finally {
			closeConnection();
		}
	}

        @Override
	public Person get(String email) {
		Person person = null;
		try {
			String sql = "SELECT * FROM " + TABLE + " WHERE email=?";
			initializeStatement(sql);
			statement.setString(1, email);
			ResultSet result = statement.executeQuery();
                        logger.writeInfo("Executed SQL statement, for GET on remote DB, table " + TABLE);
			while (result.next()) {
				String name = result.getString("name");
				String password = result.getString("password");
				person = new Person(name, email, password);
                                person.setPassword(password);
			}
		} catch (SQLException e) {
                        logger.writeFatal("exception in get user remote userDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return person;
	}

        @Override
	public List<Person> getAll() {
		List<Person> persons = new ArrayList<Person>();
		try {
			String sql = "SELECT * FROM " + TABLE;
			initializeStatement(sql);
			ResultSet result = statement.executeQuery();
                        logger.writeInfo("Executed SQL statement, for GET ALL on remote DB, table " + TABLE);
			while (result.next()) {
				String name = result.getString("name");
				String email = result.getString("email");
				String password = result.getString("password");
				Person person = new Person(name, email, password);
				persons.add(person);
			}
		} catch (SQLException e) {
                        logger.writeFatal("exception getting all users in remote userDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return persons;
	}

	private void initializeStatement(String sql) throws SQLException {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UserDb.class.getName()).log(Level.SEVERE, null, ex);
            }
                logger.writeInfo("Initializing SQL statement [" + sql + "] for remote DB, table " + TABLE);
                
                logger.writeInfo("Initializing SQL statement getting URL, table " + TABLE);
		String url = getProperties().getProperty("url");
                
                logger.writeInfo("Initializing SQL statement getting connection, table " + TABLE);
		connection = DriverManager.getConnection(url, getProperties());
                
                logger.writeInfo("Initializing SQL statement preparing statement, table " + TABLE);
		statement = connection.prepareStatement(sql);
	}

	private void closeConnection() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
                        logger.writeFatal("exception closing connection in remote userDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e.getMessage(), e);
		}
	}

	private Properties getProperties() {
		return properties;
	}

	private void setProperties(Properties properties) {
		if(properties == null){
			throw new DbException("No properties given");
		}
		this.properties = properties;
	}
}
