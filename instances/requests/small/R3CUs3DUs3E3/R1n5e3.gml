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
    prc 4
    ant 1
    prb 2
    x 38
    y 91
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 1
    prb 5
    x 58
    y 42
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 1
    prb 3
    x 39
    y 63
  ]
  edge [
    source 0
    target 4
    bandwith 698
    delay 386
  ]
  edge [
    source 1
    target 2
    bandwith 652
    delay 316
  ]
  edge [
    source 1
    target 3
    bandwith 134
    delay 325
  ]
]
