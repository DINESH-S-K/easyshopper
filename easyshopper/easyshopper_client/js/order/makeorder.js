import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId == 3) {
    fetchOrderData()
  }
  else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});

let orderId;
document.getElementById('purchaseOrderButton').addEventListener('click', onPurchaseOrder);
document.getElementById('cancelOrderButton').addEventListener('click', onCancelOrder);

function fetchOrderData() {
    fetch(`http://localhost:8080/easyshopper_server2/viewOrder`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials:'include'
    })
        .then(response => response.json())
        .then(data => {
            orderId = data.id;
            displayOrderSummary(data)
            displayOrderItems(data.productList)
        })
        .catch(error => console.error('Error:', error));
}

// Function to display order summary in the table
function displayOrderSummary(orderSummaryData) {
    const orderSummaryTableBody = document.getElementById('orderSummaryTableBody');
    orderSummaryTableBody.innerHTML = '';

    const row = document.createElement('tr');

    const idCell = createTableCell(orderSummaryData.id);
    row.appendChild(idCell);

    const orderDateCell = createTableCell(orderSummaryData.orderDate);
    row.appendChild(orderDateCell);

    const quantityCell = createTableCell(orderSummaryData.quantity);
    row.appendChild(quantityCell);

    const totalAmountCell = createTableCell(orderSummaryData.purchasedAmount);
    row.appendChild(totalAmountCell);

    orderSummaryTableBody.appendChild(row);
}

// Function to display order items in the table
function displayOrderItems(orderItemsData) {
    const orderItemsTableBody = document.getElementById('orderItemsTableBody');
    orderItemsTableBody.innerHTML = '';

    orderItemsData.forEach(item => {
        const row = document.createElement('tr');

        const productIdCell = createTableCell(item.productName);
        row.appendChild(productIdCell);

        const quantityCell = createTableCell(item.quantity);
        row.appendChild(quantityCell);

        const unitPriceCell = createTableCell(item.unitPrice);
        row.appendChild(unitPriceCell);

        const totalPriceCell = createTableCell(item.price);
        row.appendChild(totalPriceCell);

        orderItemsTableBody.appendChild(row);
    });
}

function createTableCell(content) {
    const cell = document.createElement('td');
    cell.textContent = content;
    return cell;
}

// Function to handle the Purchase Order button click
function onPurchaseOrder() {
    console.log(`order successfullt purchased with orderId: ${orderId}`);
    window.location.href = `purchaseorder.html?orderId=${orderId}`;
}

// Function to handle the Cancel Order button click
function onCancelOrder() {
    console.log(`Cancel order with orderId: ${orderId}`);
    fetch(`http://localhost:8080/easyshopper_server2/cancelOrder?orderId=${orderId}`, {
    method: 'POST',
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
    alert('Order Cancelled');
    window.location.href = `customer.html`; 
  })
  .catch(error => {
    console.error('Error:', error);
    alert('Error occurred while processing the purchase.');
  });
}
