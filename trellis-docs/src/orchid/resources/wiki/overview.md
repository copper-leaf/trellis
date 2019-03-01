
Business logic is hard, and is often quite difficult to manage consistently across an application. Furthermore, it is
difficult to evaluate business logic in the normal manner, with a bunch of `ifs`, `switches`, and so on, when the data
needed to validate these constraints are often the result of API calls or database queries which should be run 
asynchronously. The result is a mess of callbacks and spaghetti code that is not typically managed well and ends up 
being repeated throughout the application, which makes it difficult to maintain.

_Trellis_ provides a small type-safe interface for building objects which encapsulate and validate business logic, and a 
fluent API for combining these smaller specs into larger, more complex specs. The result is that multiple conditions can 
be implemented as needed, but evaluating the complex spec is just the same as evaluating a small spec: just pass the 
object to test and a callback will eventually give you the result. And since the building and testing of the spec is now 
separated from the code that needs to validate against the spec, you can now dynamically build the spec and inject it 
with an IoC container, giving you a clean separation of concerns in your code.