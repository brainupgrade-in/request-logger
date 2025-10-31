package in.brainupgrade;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

	@Column
	String host;
	@Column
	private String sessionID;
	@Column
	private String callerIP;
	@Column
	private String originatingIP;
	@Id
	@Column
	private Date accessTime;
}
