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
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 3
    prb 4
    x 13
    y 53
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 1
    prb 2
    x 49
    y 72
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 3
    prb 2
    x 17
    y 50
  ]
  edge [
    source 0
    target 5
    bandwith 493
    delay 353
  ]
  edge [
    source 1
    target 4
    bandwith 715
    delay 311
  ]
  edge [
    source 2
    target 3
    bandwith 268
    delay 441
  ]
]
