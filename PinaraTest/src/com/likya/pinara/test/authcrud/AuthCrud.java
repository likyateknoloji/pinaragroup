package com.likya.pinara.test.authcrud;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.likya.pinara.model.PinaraAuthorization;
import com.likya.pinara.model.User;
import com.likya.pinara.model.User.RoleInfo;


public class AuthCrud extends TestCase {
	
	private PinaraAuthorization pinaraAuthorization;
	private User sampleUser;
	
	private int recordId = 0;
	
	protected void setUp() {
		try {
			pinaraAuthorization = new PinaraAuthorization();
			sampleUser = new User(RoleInfo.ADMIN, "pinara", "pinara");
			sampleUser = pinaraAuthorization.addUser(sampleUser);
			recordId = sampleUser.getId();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void testSimpleAdd() {
		User testUser = null;
		try {
			testUser = new User(RoleInfo.ADMIN, "serkan", "serkan");
		} catch (Exception e) {
			e.printStackTrace();
		}
		testUser = pinaraAuthorization.addUser(testUser);
		Assert.assertNotNull(testUser);
		
	}

	public void testSimpleReadWithId() {
		User tmpUser = pinaraAuthorization.readUser(recordId);
		Assert.assertNotNull(tmpUser);
	}

	public void testSimpleReadWithUsername() {
		User tmpUser = pinaraAuthorization.readUser(sampleUser.getUsername());
		Assert.assertNotNull(tmpUser);
	}

	public void testSimpleUpdate() {
		User tmpUser = pinaraAuthorization.updateUser(sampleUser);
		Assert.assertNotNull(tmpUser);
	}
	
	public void testSimpleDelete() {
		User tmpUser = pinaraAuthorization.deleteUser(sampleUser);
		Assert.assertNotNull(tmpUser);
	}
}
