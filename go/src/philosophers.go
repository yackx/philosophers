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

type Fork struct{ sync.Mutex }

type Philosopher struct {
	id          int
	left, right *Fork
}

// Endlessly dine.
// Goes from thinking to hungry to eating and starts over.
// Adapt the pause values to increased or decrease contentions
// around the forks.
func (p Philosopher) dine() {
	say("thinking", p.id)
	randomPause(2)

	say("hungry", p.id)
	p.left.Lock()
	p.right.Lock()

	say("eating", p.id)
	randomPause(5)

	p.right.Unlock()
	p.left.Unlock()

	p.dine()
}

func randomPause(max int) {
	time.Sleep(time.Millisecond * time.Duration(rand.Intn(max*1000)))
}

func say(action string, id int) {
	fmt.Printf("#%d is %s\n", id, action)
}

func init() {
	// Random seed
	rand.Seed(time.Now().UTC().UnixNano())
}

func main() {
	// How many philosophers and forks
	count := 5

	// Create forks
	forks := make([]*Fork, count)
	for i := 0; i < count; i++ {
		forks[i] = new(Fork)
	}

	// Create philosopher, assign them 2 forks and send them to the dining table
	philosophers := make([]*Philosopher, count)
	for i := 0; i < count; i++ {
		philosophers[i] = &Philosopher{
			id: i, left: forks[i], right: forks[(i+1)%count]}
		go philosophers[i].dine()
	}

	// Wait endlessly while they're dining
	endless := make(chan int)
	<-endless
}
