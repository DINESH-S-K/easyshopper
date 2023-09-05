import AuthUtility from "../authutility.js";

let orderId;
AuthUtility.userInfo().then(data => {
  console.log(data);
  if (data && data.roleId == 3) {
    const urlParams = new URLSearchParams(window.location.search);
    orderId = urlParams.get('orderId')
  }
  else {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});


document.getElementById('purchaseButton').addEventListener('click', onPurchaseButtonClicked);

function onPurchaseButtonClicked() {
  const pinInput = document.getElementById('pin');
  const pin = pinInput.value;

  fetch(`http://localhost:8080/easyshopper_server2/purchaseOrder?orderId=${orderId}&pin=${pin}`, {
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
      alert('Purchase successful.');
      window.location.href = `customer.html`;
    })
    .catch(error => {
      console.error('Error:', error);
      alert('Error occurred while processing the purchase.');
    });
}
