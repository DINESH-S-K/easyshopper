import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
  console.log(data);
  if (!data && data.roleId !== 3) {
    alert("Access denied. You are not authorized to access this page.");
    window.location.href = 'login.html';
  }
});

document.getElementById('openWalletButton').addEventListener('click', onOpenWalletButtonClicked);
document.getElementById('backButton').addEventListener('click', onBackButtonClicked);

function onOpenWalletButtonClicked() {
  const pin = document.getElementById('pin').value;
  fetch(`http://localhost:8080/easyshopper_server2/createWalletAccount?pin=${pin}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include'
  })
    .then(response => {
      if (!response.ok && response.status != 201) {
        throw new Error('Network response was not ok');
      }
      alert('Wallet successfully created.');
      window.history.back();
    })
    .catch(error => {
      console.error('Error:', error);
      alert('Error occurred while creating the wallet.');
    });

}

function onBackButtonClicked() {
  window.history.back();
}
