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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 4
    prb 6
    x 101
    y 22
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 5
    prb 5
    x 20
    y 76
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 6
    prb 2
    x 52
    y 22
  ]
  edge [
    source 0
    target 3
    bandwith 250
    delay 152
  ]
  edge [
    source 1
    target 4
    bandwith 741
    delay 400
  ]
  edge [
    source 1
    target 2
    bandwith 334
    delay 286
  ]
]
