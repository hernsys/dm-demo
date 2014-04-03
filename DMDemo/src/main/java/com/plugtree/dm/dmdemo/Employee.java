package com.plugtree.dm.dmdemo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Employee {
	private Integer id;
	private String name;
	private String phone;
	private String email;
	private BigDecimal basicSalary;
	private Date jobStartDate;
	private String position;
	private Role role;
	private Employee directSupervisor;
	private List<Employee> reports;

	public Employee() {

	}

	private Employee(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.phone = builder.phone;
		this.email = builder.email;
		this.basicSalary = builder.basicSalary;
		this.jobStartDate = builder.jobStartDate;
		this.position = builder.position;
		this.role = builder.role;
		this.directSupervisor = builder.directSupervisor;
		this.reports = builder.reports;
	}

	
	@Override
	public String toString() {
		return name + "[" + role + "]";
	}

	/**
	 * Returns this employee's supervisor of the role sent as parameter. If it is not found, returns null
	 * 
	 * @param role Role of the supervisor to be found
	 * @return the Supervisor of the role sent as parameter, or null if is is not found
	 */
	public Employee getSupervisor(Role role) {
		if (directSupervisor == null) {
			return null;
		}
		if (role.equals(directSupervisor.getRole())) {
			return directSupervisor;
		}
		else {
				return directSupervisor.getSupervisor(role);
		}
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(BigDecimal basicSalary) {
		this.basicSalary = basicSalary;
	}

	public Date getJobStartDate() {
		return jobStartDate;
	}

	public void setJobStartDate(Date jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Employee getDirectSupervisor() {
		return directSupervisor;
	}

	public void setDirectSupervisor(Employee directSupervisor) {
		this.directSupervisor = directSupervisor;
	}

	public List<Employee> getReports() {
		return reports;
	}

	public void setReports(List<Employee> reports) {
		this.reports = reports;
	}

	public static class Builder {
		private Integer id;
		private String name;
		private String phone;
		private String email;
		private BigDecimal basicSalary;
		private Date jobStartDate;
		private String position;
		private Role role;
		private Employee directSupervisor;
		private List<Employee> reports;

		public Builder(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Employee build() {
			return new Employee(this);
		}

		public Builder phone(String phone) {
			this.phone = phone;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder basicSalary(BigDecimal basicSalary) {
			this.basicSalary = basicSalary;
			return this;
		}

		public Builder jobStartDate(Date jobStartDate) {
			this.jobStartDate = jobStartDate;
			return this;
		}

		public Builder position(String position) {
			this.position = position;
			return this;
		}

		public Builder role(Role role) {
			this.role = role;
			return this;
		}

		public Builder directSupervisor(Employee directSupervisor) {
			this.directSupervisor = directSupervisor;
			return this;
		}

		public Builder reports(List<Employee> reports) {
			this.reports = reports;
			return this;
		}
	}

}
