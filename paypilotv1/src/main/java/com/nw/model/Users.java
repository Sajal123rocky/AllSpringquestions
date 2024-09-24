package com.nw.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class Users {
	
		@Id
	 	private String userId;
		@Column
	    private String email;
		@Column
	    private String password;
		@Column
	    private String panDetails;
		@Column
	    private String bankAccountNumber;
		@Column
	    private String ifscCode;
		@Column
	    private String bankingPartner;
	   
		 @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
		    private List<RefreshToken> refreshTokens;
	    public Users() {}
	    //Parameterized Constructor 
	    public Users(String userId, String email, String password, String panDetails, String bankAccountNumber,
				String ifscCode, String bankingPartner) {
			this.userId = userId;
			this.email = email;
			this.password = password;
			this.panDetails = panDetails;
			this.bankAccountNumber = bankAccountNumber;
			this.ifscCode = ifscCode;
			this.bankingPartner = bankingPartner;
			 
		}
	    
		public List<RefreshToken> getRefreshTokens() {
			return refreshTokens;
		}
		public void setRefreshTokens(List<RefreshToken> refreshTokens) {
			this.refreshTokens = refreshTokens;
		}
		// Getters and Setters
	    public String getUserId() {
	        return userId;
	    }

	    public void setUserId(String userId) {
	        this.userId = userId;
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

	    public String getPanDetails() {
	        return panDetails;
	    }

	    public void setPanDetails(String panDetails) {
	        this.panDetails = panDetails;
	    }

	    public String getBankAccountNumber() {
	        return bankAccountNumber;
	    }

	    public void setBankAccountNumber(String bankAccountNumber) {
	        this.bankAccountNumber = bankAccountNumber;
	    }

	    public String getIfscCode() {
	        return ifscCode;
	    }

	    public void setIfscCode(String ifscCode) {
	        this.ifscCode = ifscCode;
	    }

	    public String getBankingPartner() {
	        return bankingPartner;
	    }

	    public void setBankingPartner(String bankingPartner) {
	        this.bankingPartner = bankingPartner;
	    }
		
	
	
}
