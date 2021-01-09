graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 3
    prb 4
    x 73
    y 21
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 5
    prb 4
    x 33
    y 90
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 10
    prb 2
    x 26
    y 102
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 2
    prb 2
    x 109
    y 23
  ]
  edge [
    source 0
    target 2
    bandwith 561
    delay 493
  ]
  edge [
    source 0
    target 3
    bandwith 404
    delay 290
  ]
  edge [
    source 1
    target 4
    bandwith 927
    delay 109
  ]
  edge [
    source 1
    target 5
    bandwith 387
    delay 281
  ]
]
