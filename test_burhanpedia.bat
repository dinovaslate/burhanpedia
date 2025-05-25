@echo off
echo === Kompilasi file utama dan test ===
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\core\menu\MainMenuSystem.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\core\command\SystemCommand.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\annotation\ProductValidation.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\annotation\ProductValidator.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\user\User.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\user\Admin.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\user\Pembeli.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\user\Penjual.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\user\Pengirim.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\cart\CartProduct.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\cart\Cart.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\product\Product.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\discount\Voucher.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\discount\Promo.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\transaction\TransactionStatus.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\transaction\TransaksiProduct.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\model\transaction\Transaksi.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\DiskonRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\AdminRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\UserRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\ProductRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\TransaksiRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\VoucherRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\repository\PromoRepository.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\service\SystemAdmin.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\service\SystemPembeli.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\service\SystemPenjual.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\service\SystemPengirim.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\Burhanpedia.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\exception\*.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\main\java\com\burhanpedia\util\*.java

echo === Kompilasi file test ===
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\BurhanpediaTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\annotation\ProductValidatorTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\core\menu\MainMenuSystemTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\model\discount\PromoTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\model\discount\VoucherTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\model\transaction\TransaksiTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\model\user\UserTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\model\cart\CartTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\model\product\ProductTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\repository\ProductRepositoryTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\repository\TransaksiRepositoryTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\repository\UserRepositoryTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\repository\AdminRepositoryTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\repository\VoucherRepositoryTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\repository\PromoRepositoryTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\service\SystemAdminTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\service\SystemPembeliTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\service\SystemPenjualTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\service\SystemPengirimTest.java
javac -d bin -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src\test\java\com\burhanpedia\util\DateManagerTest.java

echo === Menjalankan semua test ===
java -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ^
com.burhanpedia.BurhanpediaTest ^
com.burhanpedia.core.menu.MainMenuSystemTest ^
com.burhanpedia.annotation.ProductValidatorTest ^
com.burhanpedia.model.discount.PromoTest ^
com.burhanpedia.model.discount.VoucherTest ^
com.burhanpedia.model.transaction.TransaksiTest ^
com.burhanpedia.model.user.UserTest ^
com.burhanpedia.model.cart.CartTest ^
com.burhanpedia.model.product.ProductTest ^
com.burhanpedia.repository.ProductRepositoryTest ^
com.burhanpedia.repository.TransaksiRepositoryTest ^
com.burhanpedia.repository.UserRepositoryTest ^
com.burhanpedia.repository.AdminRepositoryTest ^
com.burhanpedia.repository.VoucherRepositoryTest ^
com.burhanpedia.repository.PromoRepositoryTest ^
com.burhanpedia.service.SystemAdminTest ^
com.burhanpedia.service.SystemPembeliTest ^
com.burhanpedia.service.SystemPenjualTest ^
com.burhanpedia.service.SystemPengirimTest ^
com.burhanpedia.util.DateManagerTest

echo === Selesai ===
pause