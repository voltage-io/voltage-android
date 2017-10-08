# Design Notes

##### NEW USER EXPERIENCE
* This still needs work
* Consider sending reg ids to a server to make them searchable
* Each user could be represented by a unique list of random words
* This allows for anonymity and a better new user experience

##### DELAYED MESSAGES
* Allow users to send messages in the future
* Need to add a table which holds dates for future messages
* Sync adapter is already in place but needs to cycle over pending messages

##### SENDING MESSAGES
* Maybe all messages should be added to a separate table
* This way failed messages can automatically be resent in the future
* Only once a message is sent does it get removed from the table
* This can work the same way as delayed messages where the sync adapter sends messages

##### GROUP MESSAGE RECEIPTS
* Need to figure out read receipts
* Or add some type of user progress in the conversation

##### TRANSACTIONS VIEW
* Need to investigate performance of the TransactionView
* Subqueries within an inner join are not great in sqlite

##### ENCRYPTION
###### [Option 1]
* Generate a keypair for each user
* Encrypt each message with the recipients public key
* Problem is that each message can only ever be sent to one user at a time

###### [Option 2]
* Generate a keypair for each user
* Generate a symmetric key for each conversation
* Encrypt each symmetric key and send it to each member of the conversation
* Encrypt each message in the conversation with the symmetric key