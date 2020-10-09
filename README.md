# ApolloRootQueryFragmentBug

This project demonstrates a bug with the Apollo Android Compiler (v2.4.1).

To demonstrate the bug, we will use a custom `OkHttpClient` to see the actual requests being made, and an openly hosted GraphQL Star Wars API.

Given the following queries

```graphql
query PlanetRootQueryBug {
  ...rootPlanetQueryFragment
}

query PlanetRootQueryWorks {
  planet(planetID: 1) {
    ...planetFragment
  }
}

fragment rootPlanetQueryFragment on Root {
  planet(planetID: 1) {
    ...planetFragment
  }
}

fragment planetFragment on Planet {
  name
  diameter
  rotationPeriod
  orbitalPeriod
}

```

the following `QUERY_DOCUMENT`s are generated for each query type `PlanetRootQueryBugQuery` and `PlanetRootQueryWorksQuery`:

`PlanetRootQueryBugQuery`
```java
public static final String QUERY_DOCUMENT = QueryDocumentMinifier.minify(
    "query PlanetRootQueryBug {\n"
        + "  ...rootPlanetQueryFragment\n"
        + "}"
  );
```

`PlanetRootQueryWorksQuery`
```java
public static final String QUERY_DOCUMENT = QueryDocumentMinifier.minify(
    "query PlanetRootQueryWorks {\n"
        + "  planet(planetID: 1) {\n"
        + "    __typename\n"
        + "    ...planetFragment\n"
        + "  }\n"
        + "}\n"
        + "fragment planetFragment on Planet {\n"
        + "  __typename\n"
        + "  name\n"
        + "  diameter\n"
        + "  rotationPeriod\n"
        + "  orbitalPeriod\n"
        + "}"
  );
```

As can be seen in the above snippets, `rootPlanetQueryFragment` is missing from the `QUERY_DOCUMENT` definition for `PlanetRootQueryBugQuery`.
This can be confimed by inspecting logs from our OkHttp `Interceptor`:

```
2020-10-09 12:21:11.625 5454-5485/me.tylerbwong.apollorootqueryfragmentbug D/APOLLO_REQUEST_BODY: {"operationName":"PlanetRootQueryBug","variables":{},"query":"query PlanetRootQueryBug { ...rootPlanetQueryFragment }"}
2020-10-09 12:21:11.625 5454-5484/me.tylerbwong.apollorootqueryfragmentbug D/APOLLO_REQUEST_BODY: {"operationName":"PlanetRootQueryWorks","variables":{},"query":"query PlanetRootQueryWorks { planet(planetID: 1) { __typename ...planetFragment } } fragment planetFragment on Planet { __typename name diameter rotationPeriod orbitalPeriod }"}
```
