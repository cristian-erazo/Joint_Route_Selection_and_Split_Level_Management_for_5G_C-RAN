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
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 8
    prb 3
    x 69
    y 17
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 5
    prb 5
    x 90
    y 112
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 10
    prb 5
    x 42
    y 97
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 10
    prb 3
    x 46
    y 25
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 3
    prb 3
    x 56
    y 27
  ]
  edge [
    source 0
    target 4
    bandwith 769
    delay 195
  ]
  edge [
    source 0
    target 5
    bandwith 412
    delay 425
  ]
  edge [
    source 1
    target 2
    bandwith 925
    delay 206
  ]
  edge [
    source 1
    target 6
    bandwith 163
    delay 465
  ]
  edge [
    source 1
    target 3
    bandwith 118
    delay 481
  ]
]
