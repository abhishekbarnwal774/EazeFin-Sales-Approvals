const requestAmountInput = document.getElementById('request-amount');
const returnAmountInput = document.getElementById('return-amount');
const submitRequestBtn = document.getElementById('submit-request');
const submitReturnBtn = document.getElementById('submit-return');
const cancelRequestBtns = document.querySelectorAll('.cancel-btn[data-is-request="true"]');
const cancelReturnBtns = document.querySelectorAll('.cancel-btn[data-is-request="false"]');

submitRequestBtn.addEventListener('click', () => {
	const requestAmount = requestAmountInput.value;
	if (requestAmount <= 0) {
		alert('Request Amount should be greater than 0!');
		return false;
	}
	else {
		sendSalesRequest('request', requestAmount);
	}
});

submitReturnBtn.addEventListener('click', () => {
	const returnAmount = returnAmountInput.value;
	if (returnAmount <= 0) {
		alert('Return Amount should be greater than 0!');
		return false;
	}
	else {
		sendSalesRequest('return', returnAmount);
	}
});

cancelRequestBtns.forEach(btn => {
	btn.addEventListener('click', () => {
		const salesId = btn.dataset.salesId;
		removeRequestFromUI(salesId);
	});
});

cancelReturnBtns.forEach(btn => {
	btn.addEventListener('click', () => {
		const salesId = btn.dataset.salesId;
		removeReturnFromUI(salesId);
	});
});

function sendSalesRequest(requestType, amount) {
	fetch('/employee/sales-request', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ requestType, amount })
	})
		.then(response => response.json())
		.then(data => {
			if (data.status === 'success') {
				// Update the UI with the new request or return
				location.reload();
			} else {
				alert(data.message);
			}
		})
		.catch(error => {
			console.error('Error:', error);
			alert('An error occurred. Please try again later.');
		});

}

function removeRequestFromUI(salesTransactionId) {
	const requestRow = document.querySelector(`.requests-table tr[data-sales-id="${salesTransactionId}"]`);
	if (requestRow) {
		requestRow.remove();
	}
}

function removeReturnFromUI(salesId) {
	const returnRow = document.querySelector(`.returns-table tr[data-sales-id="${salesId}"]`);
	if (returnRow) {
		returnRow.remove();
	}
}