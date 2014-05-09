package com.plugtree.dm.entities;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The Approval of a LeaveRequest by an Approver.
 * 
 * @author Ezequiel Grande
 *
 */
public class LeaveApproval implements Serializable {
	private static final long serialVersionUID = 772912663993617202L;
	private static final AtomicLong idSeq = new AtomicLong(0);
	
	private final long id = idSeq.incrementAndGet();

	private ApprovalType type;
	private Employee approver;
	private LeaveRequest request;
	
	/**
	 * Creates a Pending Approval for the given Request and Approver
	 * 
	 * @param request the LeaveRequest
	 * @param approver the Approver
	 */
	private LeaveApproval(LeaveRequest request, Employee approver) {
		this.request = request;
		this.approver = approver;
		this.type = ApprovalType.PENDING;
	}
	
	public long getId() {
		return id;
	}

	public ApprovalType getType() {
		return type;
	}
	public void setType(ApprovalType type) {
		this.type = type;
	}
	public Employee getApprover() {
		return approver;
	}
	public void setApprover(Employee approver) {
		this.approver = approver;
	}
	public LeaveRequest getRequest() {
		return request;
	}
	public void setRequest(LeaveRequest request) {
		this.request = request;
	}
	
	@Override
	public String toString() {
		return "[LeaveApproval = " + type + " - " + approver.getName() +"]";
	}

	public static LeaveApproval newDirectSupervisorLeaveApproval(LeaveRequest leaveRequest) {
		return new LeaveApproval(leaveRequest, leaveRequest.getEmployee().getDirectSupervisor());
	}
	
	public static LeaveApproval newLeaveApproval(LeaveRequest request, Employee approver) {
		return new LeaveApproval(request, approver);
	}
}
