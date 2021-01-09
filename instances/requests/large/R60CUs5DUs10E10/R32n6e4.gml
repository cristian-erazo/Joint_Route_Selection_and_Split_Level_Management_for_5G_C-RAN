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
    type 1
    prc 3
    ant 6
    prb 3
    x 51
    y 90
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 4
    prb 3
    x 105
    y 30
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 10
    prb 5
    x 59
    y 70
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 4
    prb 2
    x 33
    y 105
  ]
  edge [
    source 0
    target 5
    bandwith 788
    delay 247
  ]
  edge [
    source 0
    target 3
    bandwith 319
    delay 425
  ]
  edge [
    source 1
    target 2
    bandwith 467
    delay 315
  ]
  edge [
    source 1
    target 4
    bandwith 118
    delay 126
  ]
]
