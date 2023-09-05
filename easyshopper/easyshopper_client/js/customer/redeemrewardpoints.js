import AuthUtility from "../authutility.js";

AuthUtility.userInfo().then(data => {
    console.log(data);
    if (data && data.roleId == 3) {
        fetchRewardPointsAndBalance()
    }
    else {
        alert("Access denied. You are not authorized to access this page.");
        window.location.href = 'login.html';
    }
});


document.getElementById('submitRedeemButton').addEventListener('click', onRedeemButtonClicked);
document.getElementById('backButton').addEventListener('click', onBackButtonClicked);


function fetchRewardPointsAndBalance() {

    fetch(`http://localhost:8080/easyshopper_server2/getRewardPointsAndBalance`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log(data.oldBalance);
            displayRewardPointsAndBalance(data.rewardPoints, data.oldBalance);
        })
        .catch(error => {
            console.error('Error:', error);
            displayRewardPointsAndBalance('Error occurred while fetching reward points.', 'Error occurred while fetching old balance.');
        });

}

// Display reward points and old balance
function displayRewardPointsAndBalance(rewardPoints, oldBalance) {
    const rewardPointsElement = document.getElementById('rewardPoints');
    rewardPointsElement.textContent = rewardPoints;

    const oldBalanceElement = document.getElementById('oldBalance');
    oldBalanceElement.textContent = `$${oldBalance.toFixed(2)}`;
}

function onRedeemButtonClicked() {
    const pinInput = document.getElementById('pin');
    const pin = pinInput.value;
    console.log(pin);

    fetch(`http://localhost:8080/easyshopper_server2/redeemRewardPoints?pin=${pin}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            alert(data.msg)
            fetchNewBalance()
        })
        .catch(error => {
            console.error('Error:', error);
            displayRewardPointsAndBalance('Error occurred while fetching reward points.', 'Error occurred while fetching old balance.');
        });


}

function fetchNewBalance() {
    const pinInput = document.getElementById('pin');
    const pin = pinInput.value;
    console.log(pin);

    fetch(`http://localhost:8080/easyshopper_server2/viewWalletBalance?pin=${pin}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            displayNewBalance(data.balance);
        })
        .catch(error => {
            console.error('Error:', error);
            displayNewBalance('Error occurred while fetching the new balance.');
        });
}

function displayNewBalance(balance) {
    const newBalanceDisplay = document.getElementById('newBalanceDisplay');
    newBalanceDisplay.textContent = `New Balance: $${balance}`;
}

function onBackButtonClicked() {
    window.history.back(); // Go back to the previous page
}
