package com.example.SalesApprovals.Test1.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class SalesApprovalController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/sales-approval")
    public String getSalesApprovalPage(Model model) {
        List<Map<String, Object>> pendingRequests = getPendingRequests();
        List<Map<String, Object>> pendingReturns = getPendingReturns();
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("pendingReturns", pendingReturns);
        return "sales-approval";
    }

    @PostMapping("/sales-approvals/{salesId}/{isRequestApproval}")
    public @ResponseBody Map<String, List<Map<String, Object>>> updateSalesApproval(@PathVariable String salesId, @PathVariable boolean isRequestApproval, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");

        // Update the database based on the request/return and status
        if (isRequestApproval) {
            if (status.equals("approved")) {
                // Update the request status columns based on your requirements
                updateRequestStatusColumns(salesId, true);
            } else {
                // Update the request status columns based on your requirements
                updateRequestStatusColumns(salesId, false);
            }
        } else {
            if (status.equals("approved")) {
                // Update the return status columns based on your requirements
                updateReturnStatusColumns(salesId, true);
            } else {
                // Update the return status columns based on your requirements
                updateReturnStatusColumns(salesId, false);
            }
        }

        List<Map<String, Object>> pendingRequests = getPendingRequests();
        List<Map<String, Object>> pendingReturns = getPendingReturns();

        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("pendingRequests", pendingRequests);
        response.put("pendingReturns", pendingReturns);
        return response;
    }

    private void updateRequestStatusColumns(String salesId, boolean isApproved) {
        // Update the request status columns in the database based on your requirements
        // For example:
        String query = "UPDATE tbl_sales SET sales_request_status = ?, sales_display_status = ?, sales_return_status = ?, sales_request_commit = ? WHERE sales_id = ?";
        jdbcTemplate.update(query, isApproved ? false : true, isApproved ? false : true, isApproved, isApproved, salesId);
    }

    private void updateReturnStatusColumns(String salesId, boolean isApproved) {
        // Update the return status columns in the database based on your requirements
        // For example:
        String query = "UPDATE tbl_sales SET sales_request_status = ?, sales_display_status = ?, sales_return_status = ?, sales_return_commit = ? WHERE sales_id = ?";
        jdbcTemplate.update(query, isApproved, isApproved ? false : true, isApproved ? false : true, isApproved, salesId);
        if(!isApproved) {
        	String query1="UPDATE tbl_sales SET sales_amount_return= 0 where sales_id=? ";
        	jdbcTemplate.update(query1, salesId);
        }
    }

    private List<Map<String, Object>> getPendingRequests() {
        String query = "SELECT s.sales_id, s.sales_transaction_id, e.employee_emp_id, s.sales_amount_request " +
        		"FROM tbl_sales s " +
        		"JOIN tbl_employee e ON s.employee_id = e.employee_id " +
        		"WHERE s.sales_display_status = true AND s.sales_request_status = false AND s.sales_amount_return = 0.00 AND s.sales_return_status=false";
        return jdbcTemplate.queryForList(query);
    }

    private List<Map<String, Object>> getPendingReturns() {
        String query = "SELECT s.sales_id, s.sales_transaction_id,e.employee_emp_id, s.sales_amount_return " +
                       "FROM tbl_sales s " +
                       "JOIN tbl_employee e ON s.employee_id = e.employee_id "+
                       "WHERE sales_display_status = true AND sales_return_status = false AND sales_amount_return != 0.00";
        return jdbcTemplate.queryForList(query);
    }
}