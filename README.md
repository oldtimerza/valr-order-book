## Reasoning
- The reason for the api-testing folder is to allow a place for me to just simply store Bruno(https://www.usebruno.com) used for interacting with VALR endpoints, and my own for simple e2e testing.
- Reason behind the PriorityQueue is to use a heap for storage and retrieveal of limitorders based on bid/ask price, this is to ensure O(log n)  time complexity for retrieval. In order matching.
- The reasoning behind the AsksQueue and BidsQueue is because PriorityQueues are by default min heaps in Java I believe. So bids need to be maximal , so negative values when prioritising in the heap insert is necessary. It's also to ensure that a "correct" standard PriorityQueue is available for use with the limit orders.

## Asssumptions
- Assuming the limit orders should be removed from the book once they are matched and traded.
- Assuming the assignment wants to see break down of code to allow easier maintenance, and performance. Hence the approach to try keep the core domain as clean from Vert.X specifics as possible, while allowing an asynchronous approach.
- 

## Questions
- The time recorded for the limit order, I assume it is the Instant that the server received the request, and not a time that the client sends. We can't allow clients to send their own times, as they could "skip the queue and change priorities", but if we use server time, then from a customer user experience they might expect their exact moment of sending the request as the time for processing.

## Known missing pieces
- Many more edge case unit tests could be written for more error handling scenarios. But I was running out of time and wanted to be practical.
- Not having a database to handle automatic generation of ids and things makes it a little tricky when testing and using the In memory repositories.
- Logging
- Tracing
- Look into using mapstruct or something similar for mapping from domain model to request/response models where necessary. Did this to keep the domain clean of web specifics, in case we need to re-use the internal business logic from different contexts ( events, apis, crons, etc)