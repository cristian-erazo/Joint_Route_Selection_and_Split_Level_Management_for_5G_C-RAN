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
    type 1
    prc 3
    ant 6
    prb 3
    x 66
    y 75
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 10
    prb 4
    x 61
    y 100
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 5
    prb 6
    x 61
    y 51
  ]
  edge [
    source 0
    target 2
    bandwith 523
    delay 405
  ]
  edge [
    source 0
    target 3
    bandwith 693
    delay 368
  ]
  edge [
    source 0
    target 1
    bandwith 425
    delay 121
  ]
]
