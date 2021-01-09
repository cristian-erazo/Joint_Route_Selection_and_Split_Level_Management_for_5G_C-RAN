graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 9
    prb 5
    x 98
    y 48
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 7
    prb 2
    x 32
    y 30
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 9
    prb 5
    x 81
    y 105
  ]
  edge [
    source 0
    target 4
    bandwith 866
    delay 256
  ]
  edge [
    source 1
    target 5
    bandwith 592
    delay 267
  ]
  edge [
    source 2
    target 3
    bandwith 835
    delay 178
  ]
]
