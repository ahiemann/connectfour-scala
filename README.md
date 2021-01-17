connectfour-scala
======
[![Build Status](https://travis-ci.org/ahiemann/connectfour-scala.svg?branch=main)](https://travis-ci.org/ahiemann/connectfour-scala) [![Coverage Status](https://coveralls.io/repos/github/ahiemann/connectfour-scala/badge.svg?branch=main)](https://coveralls.io/github/ahiemann/connectfour-scala?branch=main)

connectfour-scala is a connect four game written in the Scala programming language. It currently includes:
- a text based UI
- a multi player mode
- a single player mode using a MiniMax based AI (implemented with Actors)
- an internal DSL that makes writing tests easier
- an external DSL that *could* be used for saving the current state of a game and loading it again (based on Parser Combinators)