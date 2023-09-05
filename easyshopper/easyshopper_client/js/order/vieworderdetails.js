import AuthUtility from "../authutility.js";

let orderId;
AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId == 3) {
    const urlParams = new URLSearchParams(window.location.search);
    orderId = urlParams.get('orderId')
    fetchOrderData()
  }
  else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});

function fetchOrderData() {
    fetch(`http://localhost:8080/easyshopper_server2/viewOrderDetails?orderId=${orderId}`, {
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