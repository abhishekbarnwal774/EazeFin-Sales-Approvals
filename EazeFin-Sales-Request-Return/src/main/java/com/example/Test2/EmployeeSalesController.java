package com.example.Test2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/employee")
public class EmployeeSalesController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@GetMapping("/sales-request")
	public String getSalesRequestPage(Model model, HttpServletRequest request) {
		int employeeId = getEmployeeIdFromSession(request);
		List<Map<String, Object>> pendingRequests = getPendingRequests(employeeId);
		List<Map<String, Object>> pendingReturns = getPendingReturns(employeeId);
		model.addAttribute("pendingRequests", pendingRequests);
		model.addAttribute("pendingReturns", pendingReturns);
		return "employee-sales-request";
	}

	@PostMapping("/sales-request")
	public @ResponseBody Map<String, Object> submitSalesRequest(@RequestBody Map<String, String> payload,
			HttpServletRequest request) {
		int employeeId = getEmployeeIdFromSession(request);
		String requestType = payload.get("requestType");
		double amount = Double.parseDouble(payload.get("amount"));

		boolean canRequest = canSubmitSalesRequest(employeeId);
		boolean isNewEmployee = checkIfNewEmployee(employeeId);
		if (requestType.equals("request")) {

			if (isNewEmployee) {
				createNewSalesRequest(employeeId, requestType, amount);
				return Map.of("status", "success");
			} else if (canRequest) {
				createNewSalesRequest(employeeId, requestType, amount);
				return Map.of("status", "success");
			}

			else {

				return Map.of("status", "error", "message", "You cannot submit a new request at this time.");
			}
		} else { // requestType is "return" and employee is not new

			if (!isNewEmployee) {
				boolean canSubmitReturn = canSubmitSalesReturn(employeeId);
				if (canSubmitReturn) {
					createNewSalesRequest(employeeId, requestType, amount);
					return Map.of("status", "success");
				} else {
					return Map.of("status", "error", "message", "You cannot submit a new return at this time.");
				}
			} else {
				return Map.of("status", "error", "message", "You cannot submit a new return at this time.");
			}
		}

	}

	private int getEmployeeIdFromSession(HttpServletRequest request) {
		// Implement your logic to get the employee ID from the session
		return 1; // Replace with the actual employee ID
	}

	private List<Map<String, Object>> getPendingRequests(int employeeId) {
		String query = "SELECT sales_id, sales_transaction_id, sales_amount_request " + "FROM tbl_sales "
				+ "WHERE employee_id = ? AND sales_request_commit = true order by sales_id desc";
		return jdbcTemplate.queryForList(query, employeeId);
	}

	private List<Map<String, Object>> getPendingReturns(int employeeId) {
		String query = "SELECT sales_id, sales_transaction_id, sales_amount_return " + "FROM tbl_sales "
				+ "WHERE employee_id = ? AND sales_return_commit = true order by sales_id desc";
		return jdbcTemplate.queryForList(query, employeeId);
	}

	private boolean checkIfNewEmployee(int employeeId) {
		String query = "SELECT COUNT(*) FROM tbl_sales WHERE employee_id = ?";
		int count = jdbcTemplate.queryForObject(query, Integer.class, employeeId);
		return count == 0;
	}

	private boolean canSubmitSalesRequest(int employeeId) {
		String query;

		query = "SELECT COUNT(*) FROM tbl_sales WHERE employee_id = ? AND sales_request_status = false ";

		int count = jdbcTemplate.queryForObject(query, Integer.class, employeeId);
		return count == 0;
	}

	private boolean checkIfHasExistingRequests(int employeeId) {
		String query = "SELECT COUNT(*) FROM tbl_sales WHERE employee_id = ? AND sales_request_status = true";
		int count = jdbcTemplate.queryForObject(query, Integer.class, employeeId);
		return count > 0;
	}

	private boolean canSubmitSalesReturn(int employeeId) {
		String query = "SELECT COUNT(*) FROM tbl_sales WHERE employee_id = ? AND sales_return_status = true";
		int count = jdbcTemplate.queryForObject(query, Integer.class, employeeId);
		return count > 0;
	}

	private void createNewSalesRequest(int employeeId, String requestType, double amount) {
		String query;
		if (requestType.equals("request")) {
			query = "INSERT INTO tbl_sales (sales_amount_request,employee_id) " + "VALUES (?, ?)";
		} else {
			query = "UPDATE tbl_sales set sales_amount_return = ?,sales_display_status = true,sales_return_status=false "
					+ "WHERE employee_id=? and sales_amount_return = 0 and sales_return_status = true";
		}
		jdbcTemplate.update(query, amount, employeeId);
	}
}
