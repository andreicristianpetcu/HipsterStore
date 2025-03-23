# HipsterStore

This application was generated using JHipster 8.9.0, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v8.9.0](https://www.jhipster.tech/documentation-archive/v8.9.0).

Its goal is to help me understand what is new in Java/Spring/Hibernate and maybe sell some products.

Entities and their role:

- User - A person in the app.
- Authority - Serves as roles for the user. You can see the roles being hardcoded in AuthoritiesConstants.
- Product - An item that can be purchased such as a mug or a t-shirt.
  - Fields: id, name, description;
- Price - A separate object that contains the price of a product.
  - Fields: id, value;
- ProductPrice - Keeps track of all the latest price for a specific product. It's a one to one relationship.
  - Fields: product, price;
- Order - An entity that represents a specific purchase.
  - Fields: date, subtotal, finalPrice, status, user, orderItems, discount.
- OrderItem - Stores a product and a quantity and will be part of an Order.
  - Fields: productId, priceId, quantity
- Discount - Orders have an optional discount code. This code will be generated by the admin, stored in the DB and when a customer adds a valid discount code toan order, it will be applied to the order in the final price.
  - Percentage discount, Fixed discount, "BUY_ONE_GET_ONE_FREE" discount.
  - Fields: discountCode, type, value, used

TODO:

- Implement some functions, for example: add-product, find-product, change-price or others
  - Customer facing features in CustomerController
  - Admins can call directly the resources from net.petcu.store.web.rest package
- Implement a basic authentication mechanism and role based endpoint access
  - Non-admins have ROLE_USER and can do operations on their own orders
  - Admins, which have ROLE_ADMIN, can create products/discounts etc
- Design error mechanism and handling plus logging
  - All custom exceptions extend StoreException and have HTTP status codes
  - Logging is done with SLF4J, Logback, SpringAOP and manual logs for code clarity.
- Write unit tests, at least for one class
  - CustomerServiceTest has clear and simple tests
- Use Java 17+ features
  - RandomGenerator in DummyPaymentService
- Add a small Readme to document the project ✔️

## Project Structure

Node is required for generation and recommended for development. `package.json` is always generated for a better development experience with prettier, commit hooks, scripts and so on.

In the project root, JHipster generates configuration files for tools like git, prettier, eslint, husky, and others that are well known and you can find references in the web.

`/src/*` structure follows default Java structure.

- `.yo-rc.json` - Yeoman configuration file
  JHipster configuration is stored in this file at `generator-jhipster` key. You may find `generator-jhipster-*` for specific blueprints configuration.
- `.yo-resolve` (optional) - Yeoman conflict resolver
  Allows to use a specific action when conflicts are found skipping prompts for files that matches a pattern. Each line should match `[pattern] [action]` with pattern been a [Minimatch](https://github.com/isaacs/minimatch#minimatch) pattern and action been one of skip (default if omitted) or force. Lines starting with `#` are considered comments and are ignored.
- `.jhipster/*.json` - JHipster entity configuration files

- `npmw` - wrapper to use locally installed npm.
  JHipster installs Node and npm locally using the build tool by default. This wrapper makes sure npm is installed locally and uses it avoiding some differences different versions can cause. By using `./npmw` instead of the traditional `npm` you can configure a Node-less environment to develop or test your application.
- `/src/main/docker` - Docker configurations for the application and services that the application depends on

## Development

The build system will install automatically the recommended version of Node and npm.

We provide a wrapper to launch npm.
You will only need to run this command when dependencies change in [package.json](package.json).

```
./npmw install
```

We use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./mvnw
./npmw start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `./npmw update` and `./npmw install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `./npmw help update`.

The `./npmw run` command will list all the scripts available to run for this project.
