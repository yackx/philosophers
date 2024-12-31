/*
The classical Dining philosophers problem.

Implemented with forks (aka chopsticks) as mutexes.
*/

package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

const howMany = 5
const pauseInMilliseconds = 2000

type Fork struct{ sync.Mutex }

type Philosopher struct {
	id          int
	left, right *Fork
}

func (p Philosopher) dine() {
	for {
		say("thinking", p.id)
		randomPause()
		say("hungry", p.id)
		p.left.Lock()
		p.right.Lock()
		say("eating", p.id)
		randomPause()
		p.right.Unlock()
		p.left.Unlock()
	}
}

func randomPause() {
	time.Sleep(time.Millisecond * time.Duration(rand.Intn(pauseInMilliseconds)))
}

func say(action string, id int) {
	fmt.Printf("#%d %s\n", id, action)
}

func main() {
	// Create forks
	forks := make([]*Fork, howMany)
	for i := 0; i < howMany; i++ {
		forks[i] = new(Fork)
	}

	// Create philosophers.
	// Assign them 2 forks and send them to the dining table
	philosophers := make([]*Philosopher, howMany)
	for i := 1; i <= howMany; i++ {
		left := i - 1
		right := i % howMany
		if i == howMany {
			// The last philosopher picks up the right fork first
			// to avoid a deadlock
			left, right = right, left
		}
		fmt.Println("Philosopher", i, "has forks", left, "and", right)
		philosophers[i-1] = &Philosopher{
			id: i, left: forks[left], right: forks[right],
		}
		go philosophers[i-1].dine()
	}

	// Wait endlessly while they're dining
	endless := make(chan int)
	<-endless
}
