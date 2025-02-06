<?php 
    error_reporting(E_ALL);
    include_once("./admin/ADMIN_INCLUDE/connect_db.inc.php");
    
    if (!isset($_REQUEST['token'])) {    
        die  ("Token not found");
    }
    $token = mysqli_real_escape_string($connection, $_REQUEST['token']);
    $tokenResult = mysqli_query($connection, "SELECT tokens_add_numerics_0 AS user_id, tokens_data AS exp_ts FROM tokens WHERE tokens_titlu_EN = '".$token."' LIMIT 1") or die(mysqli_error($connection));
    if(mysqli_num_rows($tokenResult)==0) die("Token not found");
    $tokenResultAssoc = mysqli_fetch_assoc($tokenResult);
    if($tokenResultAssoc['exp_ts']  < time()) die("Token expired");
    $user_id = $tokenResultAssoc['user_id'];
    
    if(isset($_REQUEST['password'])) {
        $pass = mysqli_real_escape_string($connection, $_REQUEST['password']);
        $passHash = password_hash($pass, PASSWORD_BCRYPT, ['cost' => 14]);

        mysqli_query($connection, "UPDATE users SET users_add_strings_1 = '".$passHash."' WHERE users_id = " . $user_id) or die(mysqli_error($connection));
        mysqli_query($connection, "DELETE FROM tokens WHERE tokens_add_numerics_0 = '".$user_id."'") or die(mysqli_error($connection));

        $success = true;
    }   
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Reset Password</title>
  <style>
    body {
      margin: 0;
      padding: 0;
      font-family: Arial, sans-serif;
      background-color: #f4f4f4;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }

    .form-container {
      background-color: #fff;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      width: 300px;
      text-align: center;
      border: 2px solid #5BA9AE;
    }

    .form-container img {
      width: 300px;
    }

    .form-container h2 {
      margin-bottom: 20px;
      color: #5BA9AE;
    }

    .form-container input {
      width: 280px;
      padding: 10px;
      margin-bottom: 15px;
      border: 1px solid #ccc;
      border-radius: 4px;
      font-size: 14px;
    }

    .form-container button {
      width: 100%;
      padding: 10px;
      background-color: #5BA9AE;
      color: #fff;
      border: none;
      border-radius: 4px;
      font-size: 16px;
      cursor: pointer;
    }

    .form-container button:hover {
      background-color: #4a8a8e;
    }

    .error-message {
      color: red;
      font-size: 12px;
      margin-bottom: 10px;
      display: none;
    }
  </style>
</head>
<body>
  <div class="form-container">
    <img src="/images/logo.jpg" alt="App Logo">

    <h2>
        <?php 
            if($success){
                echo "Password updated successfully.";
            } else {
                echo "Reset Password";
            }
        ?>
    </h2>
    <?php if(!$success) {  ?>
        <form id="reset-password-form">      
            <input type="hidden" id="token" name="token" value="<?php echo $token;?>" required>
            <input type="password" id="password" name="password" placeholder="New Password" required>
            <input type="password" id="confirm-password" name="confirm-password" placeholder="Confirm Password" required>
            <div class="error-message" id="error-message">Passwords do not match.</div>
            
            <button type="submit">Update Password</button>
        </form>
        
        <script>
            document.getElementById('reset-password-form').addEventListener('submit', function (event) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            const errorMessage = document.getElementById('error-message');

            if (password !== confirmPassword) {
                errorMessage.style.display = 'block';
                event.preventDefault(); // Prevent form submission
            } else {
                errorMessage.style.display = 'none';        
            }
            });
        </script>
    <?php } ?>
    </div>
</body>
</html>