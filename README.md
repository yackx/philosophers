Dining philosophers
===================

The classical [Dining philosophers problem](http://en.wikipedia.org/wiki/Dining_philosophers_problem), in Groovy and in Go.

## Go

Uses a `sync.Mutex` to represent the forks.

	youri@gyros philosophers> go run go/src/philosophers.go
	#0 is thinking
	#1 is thinking
	#2 is thinking
	#3 is thinking
	#4 is thinking
	#0 is hungry
	#0 is eating
	#3 is hungry
	#3 is eating
	#2 is hungry
	#4 is hungry

## Groovy

Uses `Semaphore`s to represent the forks.

	youri@gyros philosophers> groovy groovy/src/philosophers.groovy
	#3 is THINKING
	#5 is THINKING
	#4 is THINKING
	#2 is THINKING
	#1 is THINKING
	#2 is HUNGRY
	#4 is HUNGRY
	#4 is EATING
	#4 is THINKING
	#1 is HUNGRY
	#5 is HUNGRY

### License

[GNU General Public License version 3](http://www.gnu.org/licenses/gpl.html)