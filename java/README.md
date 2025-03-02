## Asssumptions
- Assuming the limit orders should be removed from the book once they are matched and traded.
- 

## Questions

1. The time recorded for the limit order, I assume it is the Instant that the server received the request, and not a time that the client sends. We can't allow clients to send their own times, as they could "skip the queue and change priorities", but if we use server time, then from a customer user experience they might expect their exact moment of sending the request as the time for processing.
2. 