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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 6
    prb 5
    x 19
    y 52
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 10
    prb 4
    x 109
    y 19
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 8
    prb 4
    x 73
    y 87
  ]
  edge [
    source 0
    target 3
    bandwith 115
    delay 199
  ]
  edge [
    source 1
    target 5
    bandwith 764
    delay 494
  ]
  edge [
    source 2
    target 4
    bandwith 626
    delay 364
  ]
]
