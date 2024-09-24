package com.nw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="refresh")
public class RefreshToken {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="refresh_token")
	private String refreshToken;
	
	@Column(name = "REVOKED")
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    private Users userInfo;

	public RefreshToken() {
		
	}

	public RefreshToken(Long id, String refreshToken, boolean revoked, Users userInfo) {
		super();
		this.id = id;
		this.refreshToken = refreshToken;
		this.revoked = revoked;
		this.userInfo = userInfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public Users getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Users userInfo) {
		this.userInfo = userInfo;
	}
	
	
	
}
