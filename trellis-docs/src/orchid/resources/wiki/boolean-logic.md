---
---

One of the common use-cases of the Spek API is determining whether or not a candidate object satisfies the requirements, 
and if so, it can perform some action. An example is checking user permissions, where a given user can pass if one of
the following criteria is met:

- They have been manually granted the capability
    OR
- They are in the correct role AND this specific capability has not been revoked
    OR
- They are a super-user, and have implicit permission to do anything

If the evaluation of this spec is `true`, then the user can perform that action, otherwise they are blocked from 
performing it. 

Building such a `Spek` might look like the following:

**Specification Builder**

```kotlin
val permissionSpek = HasExplicitCapabilitySpek("write")
        .or(IsRoleSpek("author").andNot(HasExplicitCapabilityRevokedSpek("write")))
        .or(IsSuperuserSpek())

val canWrite = permissionSpek.evaluate(user)
if(canWrite) {
    // perform write action
}
```

Or, using the {{ anchor('Trellis DSL') }}:

**Specification Expression**
```
cap(write) or (role(author) and not capRevoked(write)) or superuser()
```

**Configuration**

```kotlin
val context = SpekExpressionContext {
    register("role") { cxt, args ->
        IsRoleSpek(args.first().typeSafe<Any, Any, Any, String>(cxt))
    }
    register("superuser") { _, _ ->
        IsSuperuserSpek()
    }
    register("cap") { cxt, args ->
        HasExplicitCapabilitySpek(args.first().typeSafe<Any, Any, Any, String>(cxt))
    }
    register("capRevoked") { cxt, args ->
        HasExplicitCapabilityRevokedSpek(args.first().typeSafe<Any, Any, Any, String>(cxt))
    }
}
val canWrite = TrellisDsl.evaluate<User, Boolean>(
    context,
    "cap(write) or (role(author) and not capRevoked(write)) or superuser()",
    user
)
if(canWrite) {
    // perform write action
}
```
