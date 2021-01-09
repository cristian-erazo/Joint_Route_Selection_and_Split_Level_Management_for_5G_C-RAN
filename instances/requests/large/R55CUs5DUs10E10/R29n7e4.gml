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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 2
    prb 2
    x 32
    y 94
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 4
    prb 5
    x 57
    y 108
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 7
    prb 4
    x 57
    y 94
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 10
    prb 6
    x 116
    y 85
  ]
  edge [
    source 0
    target 6
    bandwith 856
    delay 322
  ]
  edge [
    source 1
    target 3
    bandwith 791
    delay 304
  ]
  edge [
    source 2
    target 5
    bandwith 275
    delay 219
  ]
  edge [
    source 2
    target 4
    bandwith 609
    delay 361
  ]
]
