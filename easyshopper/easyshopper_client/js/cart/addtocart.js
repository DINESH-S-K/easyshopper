import AuthUtility from "../authutility.js";

let productId;
AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId == 3) {
    const urlParams = new URLSearchParams(window.location.search);
    productId = urlParams.get('productId')
    console.log(productId);
    fetchProductDetails(productId);
  }
  else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});


const productNameElement = document.getElementById('productName');
const productDescriptionElement = document.getElementById('productDescription');
const productUnitPriceElement = document.getElementById('productUnitPrice');
const productQuantityInStockElement = document.getElementById('productQuantityInStock');
const quantityInput = document.getElementById('quantity');
const addToCartButton = document.getElementById('addToCartButton');



// Function to fetch product details from the API based on the product ID
function fetchProductDetails(productId) {
  fetch(`http://localhost:8080/easyshopper_server2/getProductDetails?productId=${productId}`, {
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
      productNameElement.textContent = data.productName;
      productDescriptionElement.textContent = data.description;
      productUnitPriceElement.textContent = data.unitPrice;
      productQuantityInStockElement.textContent = data.quantityInStock;
      quantityInput.max = data.quantityInStock;
    })
    .catch(error => {
      console.error('Error:', error);
      productNameElement.textContent = 'Error occurred while fetching product details.';
    });
}



function onAddToCart() {
  const price = parseFloat(productUnitPriceElement.textContent);
  const quantity = parseInt(quantityInput.value);
  const quantityInStock = parseInt(productQuantityInStockElement.textContent);

  if (quantity > quantityInStock) {
    const errorMessageElement = document.getElementById('errorMessage');
    errorMessageElement.textContent = 'Quantity exceeds available stock.';
    errorMessageElement.style.display = 'block';
    return;
  } else {
    const errorMessageElement = document.getElementById('errorMessage');
    errorMessageElement.style.display = 'none';
  }

  fetch(`http://localhost:8080/easyshopper_server2/addToCart?productId=${productId}&quantity=${quantity}&unitPrice=${price}`, {
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
      console.log('Product added to cart:', data);
      alert('Product added to cart successfully.');
      window.location.href = `customer.html`; 
    })
    .catch(error => {
      console.error('Error:', error);
      alert('Error occurred while adding the product to the cart.');
    });
}

addToCartButton.addEventListener('click', onAddToCart);

