package com.plugtree.dm.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class LeaveRequest implements Serializable {
	private static final long serialVersionUID = 5204988403115488828L;
	private static final AtomicLong idSeq = new AtomicLong(0);
	
	private final long id = idSeq.incrementAndGet();

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
	private List<LeaveApproval> approvals = new ArrayList<LeaveApproval>();
	private List<Employee> approvers = new ArrayList<Employee>();
	private List<CompensationDepartment> compensationDepartments = new ArrayList<CompensationDepartment>();

	private boolean fromExhaustedSickLeave = false;
	private String institute;
	private String relation;
	private boolean insideCountry = true;
	private int transferDistanceKm = 0;
	
	public LeaveRequest() {

	}

	public LeaveRequest(LeaveRequest other) {
		this.employee = other.employee;
		this.substitute = other.substitute;
		this.leaveType = other.leaveType;
		this.requestPayment = other.requestPayment;
		this.plannedStartDate = other.plannedStartDate;
		this.plannedEndDate = other.plannedEndDate;
		this.actualStartDate = other.actualStartDate;
		this.actualEndDate = other.actualEndDate;
		this.absenceReason = other.absenceReason;
		this.comments = other.comments;
		this.status = other.status;
		this.type = other.type;
		this.institute = other.institute;
		this.relation = other.relation;
		this.insideCountry = other.insideCountry;
		this.transferDistanceKm = other.transferDistanceKm;
	}
	
	public void setTransferDistanceKm(int transferDistanceKm) {
		this.transferDistanceKm = transferDistanceKm;
	}
	
	public int getTransferDistanceKm() {
		return transferDistanceKm;
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
		this.institute = builder.institute;
		this.relation = builder.compasionateRelation;
		this.insideCountry = builder.insideCountry;
		this.transferDistanceKm = builder.transferDistanceKm;
	}
	
	public void setInstitute(String institute) {
		this.institute = institute;
	}
	
	public String getInstitute() {
		return institute;
	}

	public String getRelation() {
		return relation;
	}
	
	public void setRelation(String relation) {
		this.relation = relation;
	}

	public boolean isInsideCountry() {
		return insideCountry;
	}
	
	public void setInsideCountry(boolean insideCountry) {
		this.insideCountry = insideCountry;
	}
	
	public boolean getInsideCountry() {
		return insideCountry;
	}
	
	public void setFromExhaustedSickLeave(boolean fromExhaustedSickLeave) {
		this.fromExhaustedSickLeave = fromExhaustedSickLeave;
	}
	
	public boolean isFromExhaustedSickLeave() {
		return fromExhaustedSickLeave;
	}
	
	public boolean getFromExhaustedSickLeave() {
		return fromExhaustedSickLeave;
	}
	
	public boolean removeCompensationDepartment(
			CompensationDepartment compensationDepartment) {
		return compensationDepartments.remove(compensationDepartment);
	}

	public boolean addCompensationDepartment(
			CompensationDepartment compensationDepartment) {
		return compensationDepartments.add(compensationDepartment);
	}

	public boolean removeApprover(Employee a) {
		return approvers.remove(a);
	}

	public long getId() {
		return id;
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

	public List<LeaveApproval> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<LeaveApproval> approvals) {
		this.approvals = approvals;
	}

	public boolean addApproval(LeaveApproval approval) {
		return approvals.add(approval);
	}

	public boolean hasPendingApprovals() {
		boolean hasPendingApprovals = false;
		for (LeaveApproval a : approvals) {
			if (ApprovalType.PENDING.equals(a.getType())) {
				hasPendingApprovals = true;
				break;
			}
		}
		return hasPendingApprovals;
	}

	public List<Employee> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<Employee> approvers) {
		this.approvers = approvers;
	}

	public boolean addApprover(Employee a) {
		return approvers.add(a);
	}

	public List<CompensationDepartment> getCompensationDepartments() {
		return compensationDepartments;
	}

	public void setCompensationDepartments(
			List<CompensationDepartment> compensationDepartments) {
		this.compensationDepartments = compensationDepartments;
	}

	public Double getLeaveLength() {
		Date actualEndDate = this.actualEndDate;
		if (actualEndDate == null) {
			actualEndDate = new Date();
		}
		long diff = actualEndDate.getTime() - actualStartDate.getTime();
		double delta = (double) diff / 3600000.;
		delta = delta / 24;
		return delta;
	}
	
	public Double getTotalAbsenceDaysUntilNow() {
		return absenceDays + getLeaveLength();
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
		private String institute;
		private String compasionateRelation;
		private Boolean insideCountry = Boolean.TRUE;
		private int transferDistanceKm = 0;

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
		
		public Builder institute(String institute) {
			this.institute = institute;
			return this;
		}
		
		public Builder insideCountry(Boolean insideCountry) {
			this.insideCountry = insideCountry;
			return this;
		}
		
		public Builder compasionateRelation(String compasionateRelation) {
			this.compasionateRelation = compasionateRelation;
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
		
		public Builder transferDistanceKm(int transferDistanceKm) {
			this.transferDistanceKm = transferDistanceKm;
			return this;
		}
	}

}
