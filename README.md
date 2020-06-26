## Merchant RESTful Software Service Assignment

####Introduction

This service is based on a seed giterate template from:
 
 `sbt new playframework/play-scala-seed.g8`

Mindful of the
 
'_simple solution representative of an enterprise deliverable_' 

requirement in the provided specification, I took the view that a Play application fulfils the role an enterprise 
deliverable (albeit assuming an organisation where Play is part of the enterprise architecture) but that some care 
would need to be taken not to obscure / blur the boundaries to my own work while adhering to the framework recommended
best practice. 

This document attempts to clarify those boundaries.


Here is a brief outline of the 'plumbing' which I did not design or write.

####Play Framework

The framework promotes MVC design. In this case the view is merely REST Json delivered over http.

Controllers are bound to http methods and URL paths (`Routes`) specified in the conf/routes file. 
While simple implementations may be provided within a Controller class, functionality may also be delegated in the 
pursuit of SOLID principles. 

The model is application code.

A Play application requires an ApplicationLoader implementation and a default Guice based implementation
is provided by the template via the addition of the guice (PlayImports.guice) dependency in build.sbt.

The specific implementation is `play.api.inject.guice.GuiceApplicationLoader`

In this way, out of the box functionality is available from the Play framework that allows the developer to concentrate
on the specifics of the tests and implementation while acquiring considerable flexibility. Hot re-loading 
during development is particularly enjoyable to anyone who has spent countless hours checking integration on 
Tomcat / Jetty etc without JRebel.

### The Service

The effective entry points to the service are the routes defined in the `conf/routes` file. These map the API to the
following acceptance specification.

####Assumptions

######The following assumptions are made:

An offer is made in the expectation that there will be corresponding orders. However, it is assumed that these will
be handled outside the scope of this service and that the intention of this service is only related to the maintenance 
of available offers and the point in time at which they expire. Usage of that information and any consequences related
to the timing of changes to offers other than for the purposes of querying the existence of and availability of offers 
is outside the scope of the service.

Accordingly it is assumed that the question of quantity of supply is outside the scope of this service and that any race 
condition between a command to expire an offer early and potential customer orders is outside the scope and should be 
addressed elsewhere (although a fuller design might address the transmission / broadcast of messages when changes are 
applied to offers within the service - _there's a fun later iteration!_).

####API description

#####createOffer [POST            /merchant/api/offer/createOffer]

1. A createOffer request should provide a description whose minimum and maximum length may be configured, a price 
and a currency mnemonic. The currency mnemonic should conform to that of a recognised i18n Locale. The price amount 
should conform to two decimal places, may not be negative and will not be rounded by the service. 
All parameters are required and must conform to the described invariants or an error response will be returned.

2. A createOffer request should define the "length of time an offer is valid for". However, the business definition does 
not describe the measure of time. An offer might be valid for seconds, minutes, hours, days etc. It is possible that 
simply defining the length of time would build in a critical flaw. The time taken to process the request could thwart 
the intention of the merchant e.g. the offer could expire or be detrimentally reduced depending on the length of time 
taken to process the request or be affected by the time of day (a request close to midnight might feasibly increase 
the offer expiry by a day). Assuming that the system reads the HTTP header to determine the instant when the request 
was sent it would be possible to be more accurate, but that still leaves the merchant vulnerable to the time that a 
request is sent by the client system vs the time when the request is created. It would be far less ambiguous to define 
the absolute time when the Offer should expire. 

    Therefore, I assume a preference to ensure that the expiry date/time is specified in the request.
    As JSON does not define a date / time format the expiryDateTime (and all date/time values in the service) must be 
    delivered as a string in a single designated format which may be configured.

3. A createOffer request is not required to be unique and may be duplicated. The system will return a unique identifier 
for each Offer regardless of whether an Offer with the same characteristics already exists. The assumption is that each 
validly formed request is a unique instance which should be created.

4. A createOffer request should be validated by the service to enforce its invariants. If the request is invalid an error 
response should be returned defining the cause for each error in the request

5. A valid createOffer request should cause the service to persist an Offer which should be available to query.

6. A valid createOffer request should cause the service to return a confirmatory response. The response should provide the 
identity of the Offer which has been persisted, its expiry date / time and its description.

####listOffers [POST            /merchant/api/offer/listOffers]

7. A listOffers request may be made to the system. An assumption is made that the merchant may wish to filter Offers based 
on some conditions:

    a. current, unexpired Offers
    
    b. all Offers
    
    c. expired Offers
    

There may be other valid conditions which, for simplicity we do not provide but the API should allow for further 
refinement without modifying the API.

8. A listOffer request will contain a filter command consisting of a defined string chosen singly from, '`all`', '`current`',
'`expired`'. The system will return the set of offers which fulfil the chosen criterion. Where an undefined
string is passed the system will return an error response describing the fault.

####expireOffer [GET             /merchant/api/offer/expireOffer/:id]

8. An expireOffer request should override the expiry date of an Offer. The request must provide the unique identity of the 
Offer.



