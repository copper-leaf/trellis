---
---


The core Trellis library is perfect for building your specifications in a type-safe way, but being integrated into the 
programming language means that changes to Speks mean recompiling your code. But in some cases, it may be desirable to 
build and evaluate Speks dynamically, so that they may be changes via configuration files or APIs.

Trellis has an additional module, `trellis-dsl`, that provides exactly this functionality. It has a minimal 
Specification language for building and evaluating speks in a fluent, easy-to-read format. See the following examples
for usage on setting up and using the Trellis DSL:

- {{ anchor('Boolean Logic') }}
- {{ anchor('Numeric Logic') }}

