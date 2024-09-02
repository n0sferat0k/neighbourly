///update OS
sudo apt update
sudo apt full-upgrade -y
sudo apt-get update
sudo apt-get upgrade -y

//install apache
sudo apt install golang-go -y
sudo apt install apache2 -y
sudo systemctl enable apache2
sudo systemctl start apache2

//install DB and DB admin
sudo apt install php libapache2-mod-php php-mysql -y
sudo apt-get install mariadb-server php-mysql -y
sudo apt install phpmyadmin -y
sudo mysql -u root
	ALTER USER 'root'@'localhost' IDENTIFIED BY 'qwerty1234';
	FLUSH PRIVILEGES;

//install remote desktop for PI
sudo apt install rpi-connect
rpi-connect signin

//MOVE APACHE SHIT TO THE SSD
cd /etc/apache2
sudo nano apache2.conf
	<Directory /media/neighbourly/ExtremeSSD/neighbourly/www>
        Options Indexes FollowSymLinks
        AllowOverride None
        Require all granted
	</Directory>
cd /etc/apache2/sites-available
sudo nano 000-default.conf
	DocumentRoot /media/neighbourly/ExtremeSSD/neighbourly/www
sudo systemctl restart apache2
sudo chown -R www-data:www-data /media/neighbourly/ExtremeSSD/neighbourly/www
sudo chmod -R 755 /media

//INIT AND BUILD GO MODULE 

sudo go mod init main
sudo go get github.com/go-sql-driver/mysql
sudo go get github.com/gorilla/mux
sudo go get golang.org/x/crypto/bcrypt
sudo go build -o api

sudo nano /etc/systemd/system/api.service
	[Unit]
	Description=Neighbourly Go Api
	After=network.target

	[Service]
	ExecStart=/media/neighbourly/ExtremeSSD/neighbourly/api/api
	WorkingDirectory=/media/neighbourly/ExtremeSSD/neighbourly/api
	StandardOutput=inherit
	StandardError=inherit
	Restart=always
	User=neighbourly

	[Install]
	WantedBy=multi-user.target
sudo systemctl daemon-reload
sudo systemctl enable api.service
sudo systemctl start api.service
sudo systemctl status api.service