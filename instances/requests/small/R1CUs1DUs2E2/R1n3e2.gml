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
    type 1
    prc 3
    ant 2
    prb 3
    x 80
    y 10
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 4
    prb 4
    x 44
    y 49
  ]
  edge [
    source 0
    target 2
    bandwith 361
    delay 421
  ]
  edge [
    source 0
    target 1
    bandwith 140
    delay 361
  ]
]
