// Add event listeners for buttons
import AuthUtility from "../authutility.js";

let username,id;
AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId === 3) {
    id = data.id;
    username = data.username;
    document.getElementById('welcome').innerHTML = `Hello ${username}`;
    fetchProducts()
  } else {
      alert("Access denied. You are not authorized to access this page.");
      window.location.href = 'login.html';
    }
});

function fetchProducts() {
    fetch('http://localhost:8080/easyshopper_server2/viewProducts', {
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
      displayProducts(data);
    })
    .catch(error => {
      console.error('Error:', error);
    });
  }
  
  // Function to display the products in the table
  function displayProducts(products) {
    const productsTableBody = document.getElementById('productsTableBody');
    productsTableBody.innerHTML = '';
  
    if (products.length === 0) {
      const emptyRow = document.createElement('tr');
      const emptyCell = document.createElement('td');
      emptyCell.textContent = 'No products found.';
      emptyCell.colSpan = 5;
      emptyRow.appendChild(emptyCell);
      productsTableBody.appendChild(emptyRow);
      return;
    }
  
    products.forEach(product => {
      const row = document.createElement('tr');
  
      const nameCell = createTableCell(product.productName);
      row.appendChild(nameCell);
  
      const descriptionCell = createTableCell(product.description);
      row.appendChild(descriptionCell);
  
      const unitPriceCell = createTableCell(product.unitPrice);
      row.appendChild(unitPriceCell);
  
      const quantityInStockCell = createTableCell(product.quantityInStock);
      row.appendChild(quantityInStockCell);
  
      const actionsCell = document.createElement('td');
      
      const addToCartButton = createButton('Add to Cart', () => {
        onAddToCart(product.productId);
      }, 'add-to-cart-button');
      actionsCell.appendChild(addToCartButton);
  
      row.appendChild(actionsCell);
  
      productsTableBody.appendChild(row);
    });
  }
  function createTableCell(content) {
    const cell = document.createElement('td');
    cell.textContent = content;
    return cell;
  }
  
  function createButton(text, onClick, className) {
    const button = document.createElement('button');
    button.textContent = text;
    button.addEventListener('click', onClick);
    button.classList.add(className);
    return button;
  }



document.getElementById('changePasswordButton').addEventListener('click', onChangePassword);
document.getElementById('updateProfileButton').addEventListener('click', onUpdateProfile);
document.getElementById('viewPurchaseHistoryButton').addEventListener('click', onViewPurchaseHistory);
document.getElementById('openWalletAccountButton').addEventListener('click', onOpenWalletAccount);
document.getElementById('addMoneyToWalletButton').addEventListener('click', onAddMoneyToWallet);
document.getElementById('viewWalletBalanceButton').addEventListener('click', onViewWalletBalance);
document.getElementById('redeemRewardPointsButton').addEventListener('click', onRedeemRewardPoints);
document.getElementById('logoutButton').addEventListener('click', onLogout);
document.getElementById('viewCartButton').addEventListener('click', onViewCart);

function onChangePassword() {
  console.log('Change Password clicked.');
  window.location.href = 'changepassword.html'
}

function onUpdateProfile() {
  console.log('Update Profile clicked.');
  window.location.href = `updateprofile.html`;
}

function onAddToCart(productId) {
  console.log(`Adding product with ID: ${productId} to cart.`);
  window.location.href = `addtocart.html?productId=${productId}`;
}

function onViewCart() {
  console.log('View Cart clicked.');
  window.location.href = `viewcart.html`;
}

function onOpenWalletAccount() {
  window.location.href = `createwallet.html`;
}

// Function for Add Money to Wallet button click
function onAddMoneyToWallet() {
  console.log('Add Money to Wallet clicked.');
  window.location.href = `addmoneytowallet.html`;
}

// Function for View Wallet Balance button click
function onViewWalletBalance() {
  console.log('View Wallet Balance clicked.');
  window.location.href = `viewwalletbalance.html`;
}

// Function for Redeem Reward Points button click
function onRedeemRewardPoints() {
  console.log('Redeem Reward Points clicked.');
  window.location.href = `redeemrewardpoints.html`;
}

// Function for View Purchase History button click
function onViewPurchaseHistory() {
  console.log('View Purchase History clicked.');
  window.location.href = `viewpurchasehistory.html`;
}

// Function for Logout button click
function onLogout() {
  console.log('Logout clicked.');
  window.location.href = `logout.html`;
}
  
  


