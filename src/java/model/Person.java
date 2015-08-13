package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

public class Person implements Serializable {
    
        private static final long serialVersionUID = 1L;
        private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
        
        @NotEmpty(message = "Please enter a name")
        @Size(min=2,max=30, message = "Name must be between 6 and 30 characters long")
	private String name;
        
        @NotNull
        @Pattern(regexp=EMAIL_PATTERN, message = "Email not in valid format")
        @NotEmpty(message = "Please enter an email")
	private String email;
        
        //@JsonIgnore
        @Size(min=6,max=30,message = "Password must be between 6 and 30 characters long")
	private String password;

	public Person() {}

        public Person(String email, String password) {
            this("sessionUser",email,password);
        } 
        
	public Person(String name, String email, String password) {
		setName(name);
		setEmail(email);
		setHashedPassword(password);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

        public static long getSerialVersionUID() {
            return serialVersionUID;
        }
        
        public void setHashedPassword(String password) {
		if (password == null || password.isEmpty()) {
			throw new ModelException("Password should not be empty");
		}
		this.password = hashPassword(password);
	}
	
	public boolean isPasswordCorrect(String password) {
		String hashedPassword = hashPassword(password);
                System.out.println(hashedPassword);
		return getPassword().equals(hashedPassword);
		//return getPassword().equals(password);
	}
	
	private String hashPassword(String password) {
                long passedTime;
                passedTime = System.currentTimeMillis();
		String encrypted = password;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
		} catch (NoSuchAlgorithmException e) {
			throw new ModelException(e.getMessage(), e);
		}
		//TODO Salt
		digest.update(password.getBytes());
		byte[] encryptedBytes = new byte[40];
		encryptedBytes = digest.digest();
		encrypted = new BigInteger(1, encryptedBytes).toString(16);
                System.out.println("[PERSON:INFO] It took " + (System.currentTimeMillis()-passedTime) + "ms to hash the password");
		return encrypted;
	}
        
        private static String getSalt() throws NoSuchAlgorithmException {
            //Always use a SecureRandom generator
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            //Create array for salt
            byte[] salt = new byte[16];
            //Get a random salt
            sr.nextBytes(salt);
            //return salt
            return salt.toString();
        }
}
