graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    prc 1
    ant 2
    prb 3
    x 84
    y 47
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 7
    prb 6
    x 63
    y 119
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 7
    prb 2
    x 80
    y 23
  ]
  edge [
    source 0
    target 3
    bandwith 149
    delay 223
  ]
  edge [
    source 1
    target 2
    bandwith 847
    delay 251
  ]
  edge [
    source 1
    target 4
    bandwith 391
    delay 201
  ]
]
