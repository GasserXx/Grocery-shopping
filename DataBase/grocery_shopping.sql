-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 23, 2023 at 02:48 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `grocery shopping`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `PlaceOrderItem` (IN `orderID` INT, IN `productID` INT, IN `fullPrice` REAL, IN `discount` REAL, IN `Quantity` INT)   BEGIN
    INSERT INTO Order_item (order_id, product_id, unit_price, discount, quantity)
    VALUES (orderID, productID, fullPrice, discount, Quantity);
    UPDATE Products SET purchase_count = purchase_count + Quantity WHERE product_id = productID;
END$$

--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `AddFavorite` (`user_id` INT, `product_id` INT) RETURNS INT(11)  BEGIN
    INSERT INTO Favorites (user_id, product_id)
    VALUES (user_id, product_id);
    RETURN 200;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `Authentication` (`Email` VARCHAR(255), `Password` VARCHAR(255)) RETURNS INT(11)  BEGIN
    DECLARE user_exists INT;
    DECLARE password_match INT;
    SELECT COUNT(*) INTO user_exists FROM Users WHERE e_mail = Email;
    IF user_exists = 0 THEN
        RETURN 501;
    ELSE
        SELECT COUNT(*) INTO password_match FROM Users WHERE e_mail = Email AND hashed_password = Password;
        IF password_match = 0 THEN
            RETURN 401;
        ELSE
            RETURN 200;
        END IF;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `CheckStock` (`product_id` INT, `quantity` INT) RETURNS INT(11)  BEGIN
    DECLARE stock_quantity INT;
    SELECT stock_quantity INTO stock_quantity FROM Products WHERE product_id = product_id;
    IF stock_quantity >= quantity THEN
        RETURN 200;
    ELSE
        RETURN 400;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `CheckVoucher` (`voucher` VARCHAR(255)) RETURNS INT(11)  BEGIN
    DECLARE voucher_exists INT;
    DECLARE voucher_validity BOOLEAN;
    SELECT COUNT(*) INTO voucher_exists FROM Vouchers WHERE voucher = voucher;
    IF voucher_exists = 0 THEN
        RETURN 401;
    ELSE
        SELECT validity INTO voucher_validity FROM Vouchers WHERE voucher = voucher;
        IF voucher_validity = FALSE THEN
            RETURN 501;
        ELSE
            RETURN 200;
        END IF;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `GetUserID` (`Email` VARCHAR(255)) RETURNS INT(11)  BEGIN
    DECLARE user_id INT;
    SELECT user_id INTO user_id FROM Users WHERE e_mail = Email;
    IF user_id IS NOT NULL THEN
        RETURN user_id;
    ELSE
        RETURN 400; -- User not found
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `PlaceOrder` (`voucher` VARCHAR(255), `totalPrice` REAL, `address` VARCHAR(255)) RETURNS INT(11)  BEGIN
    INSERT INTO Orders (user_id, voucher, total_price, address)
    VALUES (NULL, voucher, totalPrice, address);
    RETURN LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `RegisterAddress` (`userID` INT, `Address` VARCHAR(255), `IsPrimary` BOOLEAN) RETURNS INT(11)  BEGIN
    INSERT INTO Addresses (user_id, address, primary_address)
    VALUES (userID, Address, IsPrimary);
    RETURN 200;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `RegisterUser` (`Email` VARCHAR(255), `HashedPassword` VARCHAR(255), `FirstName` VARCHAR(255), `LastName` VARCHAR(255), `PhoneNumber` VARCHAR(20)) RETURNS INT(11)  BEGIN
    IF EXISTS (SELECT 1 FROM Users WHERE e_mail = Email) THEN
        RETURN 501; -- Email already exists
    ELSE
        INSERT INTO Users (e_mail, hashed_password, first_name, last_name, phone_number)
        VALUES (Email, HashedPassword, FirstName, LastName, PhoneNumber);
        RETURN 200; -- Return 200 on successful insertion
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `RemoveFavorite` (`user_id` INT, `product_id` INT) RETURNS INT(11)  BEGIN
    DELETE FROM Favorites WHERE user_id = user_id AND product_id = product_id;
    RETURN 200;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `user_id` int(11) NOT NULL,
  `address` varchar(255) NOT NULL,
  `primary_address` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `brands`
--

CREATE TABLE `brands` (
  `brandID` int(11) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `daily_deals`
--

CREATE TABLE `daily_deals` (
  `product_id` int(11) NOT NULL,
  `discount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `favorites`
--

CREATE TABLE `favorites` (
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `voucher` varchar(255) DEFAULT NULL,
  `total_price` double NOT NULL,
  `address` varchar(255) NOT NULL,
  `order_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `order_item`
--

CREATE TABLE `order_item` (
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `unit_price` double NOT NULL,
  `discount` double NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `brandID` int(11) NOT NULL,
  `price` double NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `subCategory_id` int(11) NOT NULL,
  `purchase_count` int(11) NOT NULL,
  `img_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `store_deals`
--

CREATE TABLE `store_deals` (
  `product_id` int(11) NOT NULL,
  `discount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subcategories`
--

CREATE TABLE `subcategories` (
  `subcategory_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `e_mail` varchar(255) NOT NULL,
  `hashed_password` varchar(255) NOT NULL,
  `phone_number` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vouchers`
--

CREATE TABLE `vouchers` (
  `voucher` varchar(255) NOT NULL,
  `percentage` double NOT NULL,
  `validity` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `brands`
--
ALTER TABLE `brands`
  ADD PRIMARY KEY (`brandID`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `daily_deals`
--
ALTER TABLE `daily_deals`
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `favorites`
--
ALTER TABLE `favorites`
  ADD KEY `user_id` (`user_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `voucher` (`voucher`);

--
-- Indexes for table `order_item`
--
ALTER TABLE `order_item`
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `brandID` (`brandID`),
  ADD KEY `subCategory_id` (`subCategory_id`);

--
-- Indexes for table `store_deals`
--
ALTER TABLE `store_deals`
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `subcategories`
--
ALTER TABLE `subcategories`
  ADD PRIMARY KEY (`subcategory_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `e_mail` (`e_mail`);

--
-- Indexes for table `vouchers`
--
ALTER TABLE `vouchers`
  ADD PRIMARY KEY (`voucher`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `brands`
--
ALTER TABLE `brands`
  MODIFY `brandID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subcategories`
--
ALTER TABLE `subcategories`
  MODIFY `subcategory_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `daily_deals`
--
ALTER TABLE `daily_deals`
  ADD CONSTRAINT `daily_deals_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `favorites`
--
ALTER TABLE `favorites`
  ADD CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `favorites_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`voucher`) REFERENCES `vouchers` (`voucher`);

--
-- Constraints for table `order_item`
--
ALTER TABLE `order_item`
  ADD CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`brandID`) REFERENCES `brands` (`brandID`),
  ADD CONSTRAINT `products_ibfk_2` FOREIGN KEY (`subCategory_id`) REFERENCES `subcategories` (`subcategory_id`);

--
-- Constraints for table `store_deals`
--
ALTER TABLE `store_deals`
  ADD CONSTRAINT `store_deals_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `subcategories`
--
ALTER TABLE `subcategories`
  ADD CONSTRAINT `subcategories_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
