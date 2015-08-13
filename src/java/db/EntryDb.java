package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import logger.DJLog;

import model.Entry;
import model.Person;

public class EntryDb implements IEntryDb{

        private Connection connection;
	private PreparedStatement statement;
	private Properties properties;
        
        private static final String TABLE = "private.r0457206_ip_entries";
        
        private final DJLog logger = new DJLog();
    
	public EntryDb(){}
	
	public EntryDb(Properties properties) {
		try {
			Class.forName("org.postgresql.Driver");
			setProperties(properties);
                        logger.writeInfo("initialized DB remotely");
		} catch (Exception e) {
                        logger.writeFatal("exception in constructor remote userDb " + e.getMessage());
			throw new DbException(e.getMessage(), e);
		}
	}

	private void initializeStatement(String sql) throws SQLException {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(EntryDb.class.getName()).log(Level.SEVERE, null, ex);
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
                        logger.writeFatal("exception closing connection in remote userDb, table " + TABLE + " " + e.getMessage());
			throw new DbException(e.getMessage(), e);
		}
	}

	private Properties getProperties() {
		return properties;
	}

	private void setProperties(Properties properties) {
		if(properties == null){
			throw new DbException("No properties given, table " + TABLE);
		}
		this.properties = properties;
	}

    @Override
    public void delete(String title, String ownerEmail) {
        if (title == null || ownerEmail == null) {
                        logger.writeWarning("Nothing to delete for remote DB, table " + TABLE);
			throw new DbException("Nothing to delete., table " + TABLE);
		}
		String sql = "DELETE FROM " + TABLE + " WHERE title=? AND owneremail=?";
		try {
			initializeStatement(sql);
                        statement.setString(1, title);
			statement.setString(2, ownerEmail);
			statement.execute();
                        logger.writeInfo("Executed SQL statement, for DELETE on remote DB, table " + TABLE);
		} catch (SQLException e) {
                        logger.writeFatal("exception in delete entry remote entryDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e);
		} finally {
			closeConnection();
		}
    }

    @Override
    public void update(Entry entry) {
        if (entry == null) {
                        logger.writeWarning("Nothing to update for remote DB, table " + TABLE);
			throw new DbException("Nothing to update., table " + TABLE);
		}
		String sql = "UPDATE " + TABLE + " SET date=?, colour=?, summary=? WHERE owneremail=?";
		try {
			initializeStatement(sql);
			statement.setString(1, entry.getDate());
			statement.setString(2, entry.getColour());
			statement.setString(3, entry.getSummary());
                        statement.setString(4, entry.getOwnerEmail());
			statement.execute();
                        logger.writeInfo("Executed SQL statement, for UPDATE on remote DB, table " + TABLE);
		} catch (SQLException e) {
                        logger.writeFatal("exception in update entry remote entryDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e);
		} finally {
			closeConnection();
		}
    }

    @Override
    public void add(Entry entry) {
        if (entry == null) {
                        logger.writeWarning("Nothing to add for remote DB, table " + TABLE);
			throw new DbException("Nothing to add., table " + TABLE);
		}
		String sql = "INSERT INTO " + TABLE + " (title, date, colour, summary, owneremail) VALUES (?, ?, ?, ?, ?)";
		try {
			initializeStatement(sql);
			statement.setString(1, entry.getTitle());
			statement.setString(2, entry.getDate());
			statement.setString(3, entry.getColour());
                        statement.setString(4, entry.getSummary());
                        statement.setString(5, entry.getOwnerEmail());
			statement.execute();
                        logger.writeInfo("Executed SQL statement, for ADD on remote DB, table " + TABLE);
		} catch (SQLException e) {
                        logger.writeFatal("exception in add entry remote entryDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e);
		} finally {
			closeConnection();
		}
    }

    @Override
    public Entry get(String title, String ownerEmail) {
        Entry entry = null;
		try {
			String sql = "SELECT * FROM " + TABLE + " WHERE title=? AND owneremail=?";
			initializeStatement(sql);
			statement.setString(1, title);
                        statement.setString(2, ownerEmail);
			ResultSet result = statement.executeQuery();
                        logger.writeInfo("Executed SQL statement, for GET on remote DB, table " + TABLE);
			while (result.next()) {
				String date = result.getString("date");
                                String colour = result.getString("colour");
                                String summary = result.getString("summary");
				entry = new Entry(title, date, ownerEmail, colour, summary);
			}
		} catch (SQLException e) {
                        logger.writeFatal("exception in get entry remote entryDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return entry;
    }

    @Override
    public List<Entry> getAll(String ownerEmail) {
        List<Entry> entries = new ArrayList<Entry>();
		try {
			String sql = "SELECT * FROM " + TABLE + " WHERE owneremail=?";
			initializeStatement(sql);
                        statement.setString(1, ownerEmail);
			ResultSet result = statement.executeQuery();
                        logger.writeInfo("Executed SQL statement, for GET ALL on remote DB, table " + TABLE);
			while (result.next()) {
                                String title = result.getString("title");
                                String date = result.getString("date");
                                String colour = result.getString("colour");
                                String summary = result.getString("summary");
				Entry entry = new Entry(title, date, ownerEmail, colour, summary);
				entries.add(entry);
			}
		} catch (SQLException e) {
                        logger.writeFatal("exception in getAll entries remote entryDb, table " + TABLE + " " +  e.getMessage());
			throw new DbException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
                Collections.sort(entries);
		return entries;
    }

}
