graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
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
    prc 3
    ant 9
    prb 4
    x 69
    y 108
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 8
    prb 3
    x 113
    y 19
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 4
    prb 2
    x 43
    y 78
  ]
  edge [
    source 0
    target 2
    bandwith 945
    delay 100
  ]
  edge [
    source 1
    target 4
    bandwith 793
    delay 442
  ]
  edge [
    source 1
    target 3
    bandwith 260
    delay 264
  ]
]
