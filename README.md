# Rynochek
#### Blockchain-like market plugin for Minecraft
##### Check it on `174.138.0.194:2137`

```yaml
name: Rynocheck
version: 2.1.3.7
description: Free market i n t e n s i f i e s

author: HenryCo
website: https://tindersamurai.dev
main: net.henryco.rynocheck.RynoCheckPlugin

commands:

  y:
    description: Confirm action
    usage: /<command>
    aliases: [yes, accept, ok, Y]

  n:
    description: Decline action
    usage: </command>
    aliases: [no, decline, cancel, N]

  wallet-login:
    description: Login to your personal wallet
    usage: /<command> {name} {password}
    aliases: [wln, wlogin, w-login]
    permissions-message: You should logout first

  wallet-logout:
    description: Logout
    usage: </command>
    aliases: [wlt, wlogout, w-logout]
    permissions: wallet_login
    permissions-message: You should login first

  wallet-create:
    description: Create new wallet account
    usage: /<command> {name} {password} {(optional) email}
    aliases: [wcr, wcreate, w-create]

  wallet-currency:
    description: Currency operations terminal
    aliases: [wcn, wcurrency, w-currency]
    usage: |
      List of <command> usage options:
       + list
       + list {page}
       + list {emitter} {page}
       + emit {recipient} {amount} {code}
       + add {name} {code} {micro} {fee} {emitter}
       + set {code} {attribute name} {new attribute value}
      {fee} - value in percents (might be NULL)
      {emitter} - emitter and fee receiver (might be NULL)
      {micro} - maximum amount of micro transaction (might be NULL)
    permission: wallet_login
    permission-message: You should login to personal wallet first.

  wallet:
    description: Your personal money wallet
    aliases: [w]
    usage: |
      List of <command> usage options:
       + send {recipient} {amount} {currency}
       + balance
       + balance {page}
       + balance {currency}
       + history
       + history {currency}
       + history {currency} {page}
      Note, that {*} argument means ALL.
    permission: wallet_login
    permission-message: You should login to personal wallet first.

permissions:
  wallet_login:
    description: Wallet login
    default: false
```