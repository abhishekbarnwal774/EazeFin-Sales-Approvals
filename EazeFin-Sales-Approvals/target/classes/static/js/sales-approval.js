const approveBtns = document.querySelectorAll('.approve-btn');
const rejectBtns = document.querySelectorAll('.reject-btn');

approveBtns.forEach(btn => {
	btn.addEventListener('click', () => {
		const salesId = btn.dataset.salesId;
		const isRequestApproval = btn.dataset.isRequestApproval === 'true';
		sendApprovalRequest(salesId, isRequestApproval, 'approved');
	});
});

rejectBtns.forEach(btn => {
	btn.addEventListener('click', () => {
		const salesId = btn.dataset.salesId;
		const isRequestApproval = btn.dataset.isRequestApproval === 'true';
		sendApprovalRequest(salesId, isRequestApproval, 'rejected');
	});
});

function sendApprovalRequest(salesId, isRequestApproval, status) {
	fetch(`/api/sales-approvals/${salesId}/${isRequestApproval}`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ status })
	})
		.then(response => response.json())
		.then(data => {
			updateRequestsTable(data.pendingRequests);
			updateReturnsTable(data.pendingReturns);
		})
		.catch(error => console.error(error));
}

function updateRequestsTable(data) {
	const requestsTableBody = document.querySelector('.requests-table tbody');
	requestsTableBody.innerHTML = '';

	data.forEach(request => {
		const row = document.createElement('tr');
		row.innerHTML = `
            
            <td>${request.sales_transaction_id}</td>
            <td>${request.employee_emp_id}</td>
            <td>${request.sales_amount_request}</td>
             
            <td>
                <button class="approve-btn" th:data-sales-id="${request.sales_id}" th:data-is-request-approval="true">
					<i class="bi bi-check-lg"></i>
				</button>
				<button class="reject-btn" th:data-sales-id="${request.sales_id}" th:data-is-request-approval="true">
					<i class="bi bi-x-lg"></i>
				</button>
            </td>
        `;
		requestsTableBody.appendChild(row);
	});

	addButtonEventListeners();
}

function updateReturnsTable(data) {
	const returnsTableBody = document.querySelector('.returns-table tbody');
	returnsTableBody.innerHTML = '';

	data.forEach(returnData => {
		const row = document.createElement('tr');
		row.innerHTML = `
            
            <td>${returnData._transaction_id}</td>
            <td>${returnData.employee_emp_id}</td>
            <td>${returnData.sales_amount_return}</td>
            
            <td>
                <button class="approve-btn"
					th:data-sales-id="${returnData.sales_id}"
					th:data-is-request-approval="false">
					<i class="bi bi-check-lg"></i>
				</button>
				<button class="reject-btn"
					th:data-sales-id="${returnData.sales_id}"
					th:data-is-request-approval="false">
					<i class="bi bi-x-lg"></i>
				</button>
            </td>
        `;
		returnsTableBody.appendChild(row);
	});

	addButtonEventListeners();
}

function addButtonEventListeners() {
	const approveBtns = document.querySelectorAll('.approve-btn');
	const rejectBtns = document.querySelectorAll('.reject-btn');

	approveBtns.forEach(btn => {
		btn.addEventListener('click', () => {
			const salesId = btn.dataset.salesId;
			const isRequestApproval = btn.dataset.isRequestApproval === 'true';
			sendApprovalRequest(salesId, isRequestApproval, 'approved');
		});
	});

	rejectBtns.forEach(btn => {
		btn.addEventListener('click', () => {
			const salesId = btn.dataset.salesId;
			const isRequestApproval = btn.dataset.isRequestApproval === 'true';
			sendApprovalRequest(salesId, isRequestApproval, 'rejected');
		});
	});
}