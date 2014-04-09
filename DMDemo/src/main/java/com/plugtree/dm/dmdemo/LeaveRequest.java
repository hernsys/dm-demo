package com.plugtree.dm.dmdemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class LeaveRequest {
	public enum Status {
		OPEN, COMPLETE, CLOSED;
	}
	
	public enum Type {
		NEW, MODIFY, CANCEL;
	}

	private Type type;
	private Employee employee;
	private Employee substitute;
	private LeaveType leaveType;
	private Boolean requestPayment;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	/**
	 * absenceDays calculated by service? Must take into account holidays,
	 * weekends, vacation balance, etc.
	 */
	private Double absenceDays;
	private AbsenceReason absenceReason;
	private String comments;
	private Status status = Status.OPEN;
	private List<Employee> approvers = new ArrayList<Employee>();
	private List<CompensationDepartment> compensationDepartments = new ArrayList<CompensationDepartment>();
	
	public LeaveRequest() {
		
	}
	
	public LeaveRequest(Builder builder) {
		this.employee = builder.employee;
		this.substitute = builder.substitute;
		this.leaveType = builder.leaveType;
		this.requestPayment = builder.requestPayment;
		this.plannedStartDate = builder.plannedStartDate;
		this.plannedEndDate = builder.plannedEndDate;
		this.actualStartDate = builder.actualStartDate;
		this.actualEndDate = builder.actualEndDate;
		this.absenceReason = builder.absenceReason;
		this.comments = builder.comments;
		this.status = builder.status;
		this.type = builder.type;
	}

	public boolean addApprover(Employee a) {
		return approvers.add(a);
	}
	
	public boolean removeCompensationDepartment(CompensationDepartment compensationDepartment) {
		return compensationDepartments.remove(compensationDepartment);
	}

	public boolean addCompensationDepartment(CompensationDepartment compensationDepartment) {
		return compensationDepartments.add(compensationDepartment);
	}
	
	public boolean removeApprover(Employee a) {
		return approvers.remove(a);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Employee getSubstitute() {
		return substitute;
	}

	public void setSubstitute(Employee substitute) {
		this.substitute = substitute;
	}

	public LeaveType getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(LeaveType leaveType) {
		this.leaveType = leaveType;
	}

	public Boolean getRequestPayment() {
		return requestPayment;
	}

	public void setRequestPayment(Boolean requestPayment) {
		this.requestPayment = requestPayment;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public Double getAbsenceDays() {
		return absenceDays;
	}

	public void setAbsenceDays(Double absenceDays) {
		this.absenceDays = absenceDays;
	}

	public AbsenceReason getAbsenceReason() {
		return absenceReason;
	}

	public void setAbsenceReason(AbsenceReason absenceReason) {
		this.absenceReason = absenceReason;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	
	
	public List<Employee> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<Employee> approvers) {
		this.approvers = approvers;
	}



	public List<CompensationDepartment> getCompensationDepartments() {
		return compensationDepartments;
	}

	public void setCompensationDepartments(
			List<CompensationDepartment> compensationDepartments) {
		this.compensationDepartments = compensationDepartments;
	}



	public static class Builder {
		private Employee employee;
		private Employee substitute;
		private LeaveType leaveType;
		private Boolean requestPayment;
		private Date plannedStartDate;
		private Date plannedEndDate;
		private Date actualStartDate;
		private Date actualEndDate;
		private AbsenceReason absenceReason;
		private String comments;
		private Status status = Status.OPEN;
		private Type type = Type.NEW;

		public Builder(Employee employee, LeaveType leaveType,
				Boolean requestPayment) {
			this.employee = employee;
			this.leaveType = leaveType;
			this.requestPayment = requestPayment;
		}
		
		public LeaveRequest build() {
			return new LeaveRequest(this);
		}

		public Builder requestPayment(Boolean requestPayment) {
			this.requestPayment = requestPayment;
			return this;
		}

		public Builder plannedStartDate(Date plannedStartDate) {
			this.plannedStartDate = plannedStartDate;
			return this;
		}

		public Builder plannedEndDate(Date plannedEndDate) {
			this.plannedEndDate = plannedEndDate;
			return this;
		}

		public Builder actualStartDate(Date actualStartDate) {
			this.actualStartDate = actualStartDate;
			return this;
		}

		public Builder actualEndDate(Date actualEndDate) {
			this.actualEndDate = actualEndDate;
			return this;
		}

		public Builder absenceReason(AbsenceReason absenceReason) {
			this.absenceReason = absenceReason;
			return this;
		}

		public Builder comments(String comments) {
			this.comments = comments;
			return this;
		}

		public Builder status(Status status) {
			this.status = status;
			return this;
		}

		public Builder type(Type type) {
			this.type = type;
			return this;
		}
	}

}
