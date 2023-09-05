import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId == 3) {
    fetchCartSummaryData()
  }
  else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});


document.getElementById('makeOrderButton').addEventListener('click', onMakeOrderButtonClicked);
document.getElementById('customerPageButton').addEventListener('click', onCustomerPageButtonClicked);

// Function to fetch cart summary data from the API
function fetchCartSummaryData() {
    fetch(`http://localhost:8080/easyshopper_server2/viewCart`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials:'include'
    })
        .then(response => response.json())
        .then(data => {
            displayCartSummaryData(data)
            displayCartItemsData(data.productList)
        })
        .catch(error => console.error('Error:', error));
}

function displayCartSummaryData(cartdata) {
    const cartSummaryTableBody = document.getElementById('cartSummaryTableBody');
    cartSummaryTableBody.innerHTML = '';
        const row = document.createElement('tr');

        const idCell = createTableCell(cartdata.id);
        row.appendChild(idCell);

        const quantityCell = createTableCell(cartdata.quantity);
        row.appendChild(quantityCell);

        const totalAmountCell = createTableCell(cartdata.totalAmount);
        row.appendChild(totalAmountCell);

        cartSummaryTableBody.appendChild(row);
    
}

function displayCartItemsData(cartItems) {
    const cartItemsTableBody = document.getElementById('cartItemsTableBody');
    cartItemsTableBody.innerHTML = '';

    cartItems.forEach(item => {
        const row = document.createElement('tr');

        const productIdCell = createTableCell(item.productName);
        row.appendChild(productIdCell);

        const quantityCell = createTableCell(item.quantity);
        row.appendChild(quantityCell);

        const unitPriceCell = createTableCell(item.unitPrice);
        row.appendChild(unitPriceCell);

        const totalPriceCell = createTableCell(item.price);
        row.appendChild(totalPriceCell);

        const removeButtonCell = document.createElement('td');
        const removeButton = createButton('Remove', () => {
            removeProductFromCart(item.productId,item.quantity,item.unitPrice);
        });
        removeButtonCell.appendChild(removeButton);
        row.appendChild(removeButtonCell);

        cartItemsTableBody.appendChild(row);
    });
}

// Helper function to create a table cell
function createTableCell(content) {
    const cell = document.createElement('td');
    cell.textContent = content;
    return cell;
}

function createButton(text, onClick) {
    const button = document.createElement('button');
    button.textContent = text;
    button.addEventListener('click', onClick);
    return button;
}

function removeProductFromCart(productId,quantity,unitPrice) {
    fetch(`http://localhost:8080/easyshopper_server2/removeProductFromCart?productId=${productId}&quantity=${quantity}&unitPrice=${unitPrice}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials:'include'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        alert("Product removed from the cart");
        window.location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error occurred while removing the product from the cart.');
    });
}

function onMakeOrderButtonClicked(){
    fetch(`http://localhost:8080/easyshopper_server2/makeOrder`, {
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
        alert(data.msg);
        window.location.href=`makeorder.html`;
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error occurred while removing the product from the cart.');
    });
}

// Function to handle the customer page button click
function onCustomerPageButtonClicked() {
    window.history.back();
}


