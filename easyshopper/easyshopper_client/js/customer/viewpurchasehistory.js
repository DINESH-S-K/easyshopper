import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId == 3) {
    fetchPurchaseHistory();
  }
  else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});


document.getElementById('backButton').addEventListener('click', onBackButtonClicked);


function fetchPurchaseHistory() {
  fetch(`http://localhost:8080/easyshopper_server2/viewPurchaseHistory`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials:'include'
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    displayPurchaseHistory(data);
  })
  .catch(error => {
    console.error('Error:', error);
    displayErrorMessage('An error occurred while fetching purchase history.');
  });
}

function displayPurchaseHistory(purchaseHistoryList) {
  const purchaseHistoryTableBody = document.getElementById('purchaseHistoryTableBody');
  purchaseHistoryTableBody.innerHTML = '';

  if (purchaseHistoryList.length === 0) {
    const emptyRow = document.createElement('tr');
    const emptyCell = document.createElement('td');
    emptyCell.textContent = 'No purchase history found.';
    emptyCell.colSpan = 5;
    emptyRow.appendChild(emptyCell);
    purchaseHistoryTableBody.appendChild(emptyRow);
    return;
  }

  purchaseHistoryList.forEach(purchase => {
    const row = document.createElement('tr');

    const idCell = createTableCell(purchase.id);
    row.appendChild(idCell);

    const orderIdCell = document.createElement('td');
    const orderIdLink = document.createElement('a');
    orderIdLink.textContent = purchase.orderId;
    orderIdLink.href = `vieworderdetails.html?orderId=${purchase.orderId}`;
    orderIdCell.appendChild(orderIdLink);
    row.appendChild(orderIdCell);
    
    // const orderIdCell = createTableCell(purchase.orderId);
    // row.appendChild(orderIdCell);

    const purchaseDateCell = createTableCell(purchase.purchaseDate);
    row.appendChild(purchaseDateCell);

    const amountDebitedCell = createTableCell(purchase.amountDebited);
    row.appendChild(amountDebitedCell);

    const walletBalanceCell = createTableCell(purchase.walletBalance);
    row.appendChild(walletBalanceCell);

    purchaseHistoryTableBody.appendChild(row);
  });
}

function createTableCell(content) {
  const cell = document.createElement('td');
  cell.textContent = content;
  return cell;
}

function displayErrorMessage(message) {
  const purchaseHistoryTableBody = document.getElementById('purchaseHistoryTableBody');
  const errorRow = document.createElement('tr');
  const errorCell = document.createElement('td');
  errorCell.textContent = message;
  errorCell.colSpan = 5;
  errorRow.appendChild(errorCell);
  purchaseHistoryTableBody.appendChild(errorRow);
}

function onBackButtonClicked() {
  window.history.back(); // Go back to the previous page
}
